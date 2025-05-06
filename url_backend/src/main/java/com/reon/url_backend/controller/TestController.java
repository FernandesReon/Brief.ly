package com.reon.url_backend.controller;

import com.reon.url_backend.models.UrlMapping;
import com.reon.url_backend.models.User;
import com.reon.url_backend.repository.UrlMappingRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final UrlMappingRepository urlMappingRepository;

    public TestController(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    @GetMapping("/urls-for-user/{userId}")
    public List<UrlMapping> getUrlsForUser(@PathVariable String userId) {
        User user = new User();
        user.setId(userId);
        return urlMappingRepository.findByUser(user, Pageable.unpaged()).getContent();
    }
}