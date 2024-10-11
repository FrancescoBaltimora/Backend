package com.triptravel.backend.users.config;

import java.io.IOException;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.triptravel.backend.users.costants.Costants;
import com.triptravel.backend.users.utils.JwtTokenUtil;
import com.triptravel.backend.users.utils.KeyUtils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtUtil; // Servizio per gestione token

	private static final Logger errLog = (Logger) LoggerFactory.getLogger("errLog");
	private static final Logger infoLog = (Logger) LoggerFactory.getLogger("infLog");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {

			// Escludi le richieste che corrispondono a /auth/**
			String path = request.getRequestURI();
			if (path.startsWith("/auth")) {
				filterChain.doFilter(request, response); // Bypassa il filtro
				return;
			}

			String token = request.getHeader("Authorization"); // Estrai token dalla richiesta
			String keyPath = Costants.PUBLIC_KEY;
			infoLog.info("request header Authorization retrieved {}", token);
			PublicKey pKey;
			pKey = KeyUtils.getPublicKey(keyPath);
			infoLog.info("get public key ", pKey);
			Claims claims = jwtUtil.verifyToken(pKey, token);
			infoLog.info("Retrieved claims", claims);
			if (token != null && claims != null) {
				// Se il token è valido, imposta l'autenticazione nel contesto di sicurezza
				infoLog.info("Valid token for user {}", claims.getSubject());
				Authentication authentication = jwtUtil.getAuthentication(claims, token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				infoLog.info("Setting request as authenticated for user {}", claims.getSubject());
			}
			filterChain.doFilter(request, response); // Passa al prossimo filtro
		} catch (Exception e) {
			errLog.error("User not authorized");
			return;
		}
	}
}