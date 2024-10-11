package com.triptravel.backend.users.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestControlle {
	
	@GetMapping("/test")
	public String getMethodName() {
		return "funziona";
	}
	
	
}
