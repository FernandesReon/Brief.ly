package com.reon.url_backend.controller;

import com.reon.url_backend.models.UrlMapping;
import com.reon.url_backend.service.impl.UrlMappingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {
    private final Logger logger = LoggerFactory.getLogger(RedirectController.class);
    private final UrlMappingServiceImpl urlMappingService;

    public RedirectController(UrlMappingServiceImpl urlMappingService) {
        this.urlMappingService = urlMappingService;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl){
        logger.info("Controller :: Incoming redirect request for shortUrl: " + shortUrl);
        UrlMapping urlMapping = urlMappingService.getOriginalUrl(shortUrl);
        if (urlMapping != null){
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Location", urlMapping.getOriginalUrl());
            return ResponseEntity.status(302).headers(httpHeaders).build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
