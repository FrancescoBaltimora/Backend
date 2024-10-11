package com.triptravel.backend.users.services;

import java.security.PrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.triptravel.backend.users.costants.Costants;
import com.triptravel.backend.users.dtos.UserDTO;
import com.triptravel.backend.users.models.Response;
import com.triptravel.backend.users.models.User;
import com.triptravel.backend.users.repository.RoleRepository;
import com.triptravel.backend.users.repository.UserRepository;
import com.triptravel.backend.users.utils.JwtTokenUtil;
import com.triptravel.backend.users.utils.KeyUtils;

@Service
public class UserService {

	@Autowired
	private BCryptPasswordEncoder encoder;
	@Autowired
	private UserRepository uRepo;
	@Autowired
	private RoleRepository rRepo;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	private static final Logger errLog = (Logger) LoggerFactory.getLogger("errLog");
	private static final Logger infoLog = (Logger) LoggerFactory.getLogger("infLog");

    public ResponseEntity<Response> register(UserDTO u) {
        try {
            if (uRepo.findByUsername(u.getUsername()) != null) {
                infoLog.info("User {} already exists", u.getUsername());
                return ResponseEntity.badRequest().body(Response.builder()
    					.code(HttpStatus.BAD_REQUEST)
    					.message("Use different credentials")
    					.build());
            }
            infoLog.info("User {} not exists", u.getUsername());
            if(!u.getPassword().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$") 
            		|| !u.getEmail().matches("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+")) {
                return ResponseEntity.badRequest().body(Response.builder()
    					.code(HttpStatus.BAD_REQUEST)
    					.message("Invalid password or email")
    					.build());
            }
            infoLog.info("Password for user {} is valid", u.getUsername());
            User user = new User(
                    u.getUsername(),
                    encoder.encode(u.getPassword()),
                    u.getEmail(),
                    rRepo.findByName("Admin"),
                    true
            );
            infoLog.info("Created user {} is valid", u.getUsername());
            uRepo.save(user);
            ResponseEntity<Response> response = generateToken(user);
            infoLog.info("User {} registered successfully", u.getUsername());
            return response;
        } catch (Exception e) {
            errLog.error("Error during user registration for: {}", u.getUsername(), e);
            return ResponseEntity.badRequest().body(Response.builder()
					.code(HttpStatus.INTERNAL_SERVER_ERROR)
					.message("Error creating user " + u.getUsername())
					.build());
        }
    }

    public ResponseEntity<Response> login(UserDTO u) {
        try {
            User user = uRepo.findByUsername(u.getUsername());
            if (user == null) {
                infoLog.info("User {} not found", u.getUsername());
                return ResponseEntity.badRequest().body(Response.builder()
    					.code(HttpStatus.BAD_REQUEST)
    					.message("Invalid credentials for user: " + u.getUsername())
    					.build());
            }
            infoLog.info("User {} found", u.getUsername());
            if (!encoder.matches(u.getPassword(), user.getPassword())) {
                infoLog.info("Invalid password for user {}", u.getUsername());
                return ResponseEntity.badRequest().body(Response.builder()
    					.code(HttpStatus.BAD_REQUEST)
    					.message("Invalid credentials for user: " + u.getUsername())
    					.build());
            }
            infoLog.info("Correct credentials for user {}", u.getUsername());
            ResponseEntity<Response> response = generateToken(user);
            infoLog.info("Login successful for user: {}", user.getUsername());
            return response;
        } catch (Exception e) {
            errLog.error("Error during user login for: {}", u.getUsername(), e);
			return ResponseEntity.badRequest().body(Response.builder()
					.code(HttpStatus.INTERNAL_SERVER_ERROR)
					.message("Error during user login for: {}" + u.getUsername())
					.build());
        }
    }
    
    public ResponseEntity<Response> generateToken(User user) {
		try {
			infoLog.info("generating tokenfor user {}", user.getUsername());
			String keyPath = Costants.PRIVATE_KEY; 
			PrivateKey key;
			key = KeyUtils.getPrivateKey(keyPath);
			String token = jwtTokenUtil.generateToken(key, user.getUsername());
			return ResponseEntity.ok(Response.builder()
					.code(HttpStatus.OK)
					.message(token)
					.build());
		} catch (Exception e) {
			errLog.error("Error Generating token for user: " + user.getUsername() + " " + e);
			return ResponseEntity.badRequest().body(Response.builder()
					.code(HttpStatus.INTERNAL_SERVER_ERROR)
					.message("Error Generating token for user: " + user.getUsername())
					.build());
		}
    }
}