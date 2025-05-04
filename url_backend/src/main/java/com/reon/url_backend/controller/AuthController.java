package com.reon.url_backend.controller;

import com.reon.url_backend.dto.LoginDTO;
import com.reon.url_backend.dto.UserRegistrationDTO;
import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.jwt.JwtAuthenticationResponse;
import com.reon.url_backend.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/public/register")
    public ResponseEntity<UserResponseDTO> registration(@Valid @RequestBody UserRegistrationDTO registrationDTO){
        logger.info("Controller :: Incoming request for registration of new user with email: " + registrationDTO.getEmail());
        UserResponseDTO newUser = userService.registerUser(registrationDTO);
        logger.info("Controller :: Registration Successful.");
        return ResponseEntity.ok().body(newUser);
    }

    @PostMapping("/public/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO){
        logger.info("Controller :: Login request from emailId: " + loginDTO.getEmail());
        JwtAuthenticationResponse jwtToken =  userService.authenticateUser(loginDTO);
        logger.info("Controller :: Login successful.");
        return ResponseEntity.ok().body(jwtToken);
    }
}
