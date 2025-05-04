package com.reon.url_backend.service.impl;

import com.reon.url_backend.dto.LoginDTO;
import com.reon.url_backend.dto.UserRegistrationDTO;
import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.exceptions.EmailAlreadyExistsException;
import com.reon.url_backend.exceptions.RestrictionException;
import com.reon.url_backend.exceptions.UsernameAlreadyExistsException;
import com.reon.url_backend.jwt.JwtAuthenticationResponse;
import com.reon.url_backend.jwt.JwtUtils;
import com.reon.url_backend.mapper.UserMapper;
import com.reon.url_backend.models.User;
import com.reon.url_backend.repository.UserRepository;
import com.reon.url_backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        /*
        Before creating a new user
        1. We will check whether an existing user as the email and username provided by the newUser.
        2. If not then setId, encrypt the password and save the newUser.
        3. Return the user response with all details.
         */
        if (userRepository.existsByEmail(registrationDTO.getEmail())){
            throw new EmailAlreadyExistsException("A user with this email already exists: " + registrationDTO.getEmail());
        }

        if (userRepository.existsByUsername(registrationDTO.getUsername())){
            throw new UsernameAlreadyExistsException("A user with this username already exists: " + registrationDTO.getUsername());
        }

        logger.info("Service :: Creating user with emailId: " + registrationDTO.getEmail());
        User user = UserMapper.mapToUser(registrationDTO);
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setCreateOn(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        logger.info("User saved.");

        return UserMapper.responseToUser(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(String id, UserRegistrationDTO update) {
        /*
        Before updating a user
        1. Check if the user exists based on the userId
        2. If exists take the new values and set it to User
        3. Return the updated response.
         */
        logger.info("Services :: Updating user with id: " + id);
        User user = userRepository.findById(id).orElseThrow(
        () -> new UsernameNotFoundException("User with id: " + id + " not found.")
    );

        if (update.getEmail() != null && !update.getEmail().isBlank()) {
            throw new RestrictionException("Updating emailId is not allowed.");
        }
        else {
            UserMapper.applyUpdates(user, update);
            user.setUpdatedOn(LocalDateTime.now());
            User savedUser = userRepository.save(user);
            logger.info("User updated successfully [id]: " + id);
            return UserMapper.responseToUser(savedUser);
        }
    }

    @Override
    public void deleteUser(String id) {
        logger.warn("Service :: Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public JwtAuthenticationResponse authenticateUser(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken((User) userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}
