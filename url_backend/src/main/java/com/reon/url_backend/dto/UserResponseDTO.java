package com.reon.url_backend.dto;

import com.reon.url_backend.models.Provider;
import com.reon.url_backend.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    /*
    This class is responsible for sending user information to currently loggedIn user.
     */
    private String id;
    private String name;
    private String username;
    private String email;
    private boolean accountEnabled;
    private boolean emailVerified;
    private Set<Role> roles;
    private Provider provider;
    private String providerId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
