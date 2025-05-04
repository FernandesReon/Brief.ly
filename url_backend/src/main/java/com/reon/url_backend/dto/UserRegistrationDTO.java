package com.reon.url_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    /*
    This class is responsible for user input data while registration process (frontend)
     */
    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Username is required.")
    @Size(max = 10, message = "Username can be maximum of 10 characters.")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 10, max = 64, message = "Password has to be minimum of 10 characters.")
    private String password;
}
