package com.reon.url_backend.mapper;

import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingResponse;
import com.reon.url_backend.models.UrlMapping;
import com.reon.url_backend.models.User;

import java.time.LocalDateTime;

public class UrlMapper {
    public static UrlMapping toEntity(UrlMappingDTO urlMappingDTO, User user, String shortUrl) {
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(urlMappingDTO.getOriginalUrl());
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setCreatedDate(LocalDateTime.now());
        urlMapping.setUser(user);
        return urlMapping;
    }

    public static UrlMappingResponse toResponse(UrlMapping urlMapping) {
        return new UrlMappingResponse(
                urlMapping.getId(),
                urlMapping.getShortUrl(),
                urlMapping.getClickedCounts(),
                urlMapping.getCreatedDate(),
                urlMapping.getUser().getEmail()
        );
    }
}
