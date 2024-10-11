package com.triptravel.backend.users.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.triptravel.backend.users.dtos.UserDTO;
import com.triptravel.backend.users.models.Response;
import com.triptravel.backend.users.services.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

	private static final Logger infoLog = (Logger) LoggerFactory.getLogger("infLog");

    @Autowired
    private UserService uService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody UserDTO user){
    	infoLog.info("Attempting to register user: {}", user.getUsername());
    	return uService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody UserDTO request) throws Exception {
    	infoLog.info("Attempting to log in user: {}", request.getUsername());
    	return uService.login(request);
    }
}