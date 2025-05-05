package com.reon.url_backend.controller;

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

@RestController
@RequestMapping("/api/urls")
public class UrlMappingController {
    private final Logger logger = LoggerFactory.getLogger(UrlMappingController.class);
    private final UrlMappingServiceImpl urlMappingService;
    private final UserServiceImpl userService;

    public UrlMappingController(UrlMappingServiceImpl urlMappingService, UserServiceImpl userService) {
        this.urlMappingService = urlMappingService;
        this.userService = userService;
    }

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingResponse> createShortUrl(@Valid @RequestBody UrlMappingDTO urlMappingDTO,
                                                             Principal principal) {
        logger.info("Creating short URL for: {}", urlMappingDTO.getOriginalUrl());
        UserResponseDTO user = userService.getUserByEmail(principal.getName());
        UrlMappingResponse urlMappingResponse = urlMappingService.createShortUrl(urlMappingDTO.getOriginalUrl(), user);
        return ResponseEntity.ok().body(urlMappingResponse);
    }

    @GetMapping("/myUrls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<UrlMappingResponse>> fetchAllPaginatedUrls(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size, Principal principal){
        logger.info("Fetching paginated URLs for user with email: {}", principal.getName());
        try {
            // Fetch the User entity by email
            User user = userService.findUserEntityByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + principal.getName()));

            // Fetch paginated URLs for the user
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
}
