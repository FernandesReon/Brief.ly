package com.reon.url_backend.service;

import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingResponse;
import com.reon.url_backend.models.User;
import org.springframework.data.domain.Page;


public interface UrlMappingService {
    public UrlMappingResponse createShortUrl(String originalUrl, UserResponseDTO user);
    Page<UrlMappingResponse> getAllPaginatedUrlsForUser(User user, int pageNo, int pageSize);
}
