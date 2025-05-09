package com.reon.url_backend.dto.urlMapping;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlMappingDTO {
    @NotBlank(message = "Original URL is required.")
    @URL(message = "Invalid URL format.")
    private String originalUrl;
}
