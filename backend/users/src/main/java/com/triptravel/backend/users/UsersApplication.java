package com.triptravel.backend.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.triptravel.backend.users.config.RateLimiterFilter;
import com.triptravel.backend.users.utils.JwtTokenUtil;

@SpringBootApplication
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}
	
    @Bean
    BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RateLimiterFilter rateLimiterFilter() {
        return new RateLimiterFilter();
    }
    
    @Bean
    JwtTokenUtil jwtTokenUtil(){
        return new JwtTokenUtil();
    }
}
