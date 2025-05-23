package com.reon.url_backend.controller;

import com.reon.url_backend.dto.urlMapping.ClickEventDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingResponse;
import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.models.User;
import com.reon.url_backend.service.impl.UrlMappingServiceImpl;
import com.reon.url_backend.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin("*")
public class UrlMappingController {
    private final Logger logger = LoggerFactory.getLogger(UrlMappingController.class);
    private final UrlMappingServiceImpl urlMappingService;
    private final UserServiceImpl userService;

    public UrlMappingController(UrlMappingServiceImpl urlMappingService, UserServiceImpl userService) {
        this.urlMappingService = urlMappingService;
        this.userService = userService;
    }

    // Create a short url
    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingResponse> createShortUrl(@Valid @RequestBody UrlMappingDTO urlMappingDTO,
                                                             Principal principal) {
        logger.info("Creating short URL for: {}", urlMappingDTO.getOriginalUrl());
        UserResponseDTO user = userService.getUserByEmail(principal.getName());
        UrlMappingResponse urlMappingResponse = urlMappingService.createShortUrl(urlMappingDTO.getOriginalUrl(), user);
        return ResponseEntity.ok().body(urlMappingResponse);
    }

    // View all urls
    @GetMapping("/myUrls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<UrlMappingResponse>> fetchAllPaginatedUrls(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size, Principal principal) {
        logger.info("Fetching paginated URLs for user with email from Principal: {}", principal.getName());
        try {
            User user = userService.findUserEntityByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + principal.getName()));
            logger.info("Fetched user with id: {} for email: {}", user.getId(), user.getEmail());
            Page<UrlMappingResponse> responsePage = urlMappingService.getAllPaginatedUrlsForUser(user, page, size);
            logger.info("Successfully retrieved {} URLs for user with email: {}",
                    responsePage.getTotalElements(), principal.getName());
            return ResponseEntity.ok(responsePage);
        } catch (UsernameNotFoundException e) {
            logger.error("Failed to fetch URLs: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while fetching URLs for user {}: {}",
                    principal.getName(), e.getMessage());
            throw new RuntimeException("Failed to fetch URLs", e);
        }
    }

    /*
    Analytics endpoints
    1. specific endpoint
    2. for specific time period
     */
    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(@PathVariable String shortUrl,
                                                               @RequestParam(value = "startDate") String startDate,
                                                               @RequestParam(value = "endDate") String endDate){
        logger.info("Controller :: Accessing analytics for specific url");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        List<ClickEventDTO> clickEventDTOS = urlMappingService.getClickEventsByDate(shortUrl, start, end);
        return ResponseEntity.ok().body(clickEventDTOS);
    }

    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(
            Principal principal,
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate) {
        logger.info("Controller :: Accessing analytics for urls");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        User user = userService.findUserEntityByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + principal.getName()));
        Map<LocalDate, Long> totalClicks = urlMappingService.getTotalClicksByUserAndDate(user, start, end);
        return ResponseEntity.ok().body(totalClicks);
    }
}
