package com.reon.url_backend.service;

import com.reon.url_backend.dto.LoginDTO;
import com.reon.url_backend.dto.UserRegistrationDTO;
import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.jwt.JwtAuthenticationResponse;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);
    UserResponseDTO updateUser(String id, UserRegistrationDTO update);
    void deleteUser(String id);
    JwtAuthenticationResponse authenticateUser(LoginDTO loginDTO);
}
