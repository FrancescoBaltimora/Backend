package com.triptravel.backend.users.utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.triptravel.backend.users.models.User;
import com.triptravel.backend.users.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtil {

	private static final Logger errLog = (Logger) LoggerFactory.getLogger("errLog");
	private static final Logger infoLog = (Logger) LoggerFactory.getLogger("infLog");

    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    @Autowired
    private UserRepository uRepo;

    public JwtTokenUtil() {
    }

    public String generateToken(PrivateKey key, String username) {
        try {
            this.privateKey = key;
            infoLog.info("Generating token for user: {}", username);
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 ora
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            errLog.error("Error generating token for user: {}", username, e);
            throw new RuntimeException("Token generation failed", e);
        }
    }
    
    public Claims verifyToken(PublicKey key, String token) {
        try {
            this.publicKey = key;
            infoLog.info("Verifying token");
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            boolean isValid = !claims.getBody().getExpiration().before(new Date());
            infoLog.info("Token verification result: {}", isValid);
            return claims.getBody();
        } catch (JwtException | IllegalArgumentException e) {
            errLog.error("Error verifying token", e);
            return null;
        }
    }
    
    public Authentication getAuthentication(Claims claims, String token) {
        User userDetails = uRepo.findByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}