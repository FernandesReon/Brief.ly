package com.reon.url_backend.service;

import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.dto.urlMapping.ClickEventDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingResponse;
import com.reon.url_backend.models.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface UrlMappingService {
    UrlMappingResponse createShortUrl(String originalUrl, UserResponseDTO user);
    Page<UrlMappingResponse> getAllPaginatedUrlsForUser(User user, int pageNo, int pageSize);

    // Analytics Services
    List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime startDate, LocalDateTime endDate);
    Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDateTime start, LocalDateTime end);
}
