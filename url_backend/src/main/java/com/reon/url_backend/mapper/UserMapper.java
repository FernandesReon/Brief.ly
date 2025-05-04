package com.reon.url_backend.mapper;

import com.reon.url_backend.dto.UserRegistrationDTO;
import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.models.User;

public class UserMapper {
    public static User mapToUser(UserRegistrationDTO register){
        User user = new User();
        user.setName(register.getName());
        user.setUsername(register.getUsername());
        user.setEmail(register.getEmail());
        user.setPassword(register.getPassword());
        return user;
    }

    public static UserResponseDTO responseToUser(User user){
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAccountEnabled(user.isAccountEnabled());
        response.setEmailVerified(user.isEmailVerified());
        response.setRoles(user.getRoles());
        response.setProvider(user.getProvider());
        response.setProviderId(user.getProviderId());
        response.setCreatedOn(user.getCreateOn());
        response.setUpdatedOn(user.getUpdatedOn());
        return response;
    }

    public static void applyUpdates(User user, UserRegistrationDTO existingUser){
        if (existingUser.getName() != null && !existingUser.getName().isBlank()){
            user.setName(existingUser.getName());
        }
        if (existingUser.getUsername() != null && !existingUser.getUsername().isBlank()){
            user.setUsername(existingUser.getUsername());
        }
        if (existingUser.getPassword() != null && !existingUser.getEmail().isBlank()){
            user.setPassword(existingUser.getPassword());
        }
    }
}
