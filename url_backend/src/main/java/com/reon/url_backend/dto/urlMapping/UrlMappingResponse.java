package com.reon.url_backend.dto.urlMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlMappingResponse {
    private Long id;
    private String shortUrl;
    private int clickCount;
    private LocalDateTime createdOn;
    private String email;
}
