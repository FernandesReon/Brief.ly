package com.reon.url_backend.service.impl;

import com.reon.url_backend.dto.UserResponseDTO;
import com.reon.url_backend.dto.urlMapping.ClickEventDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingDTO;
import com.reon.url_backend.dto.urlMapping.UrlMappingResponse;
import com.reon.url_backend.exceptions.InvalidURlException;
import com.reon.url_backend.mapper.UrlMapper;
import com.reon.url_backend.models.ClickEvent;
import com.reon.url_backend.models.UrlMapping;
import com.reon.url_backend.models.User;
import com.reon.url_backend.repository.ClickEventRepository;
import com.reon.url_backend.repository.UrlMappingRepository;
import com.reon.url_backend.repository.UserRepository;
import com.reon.url_backend.service.UrlMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UrlMappingServiceImpl implements UrlMappingService {
    private final Logger logger = LoggerFactory.getLogger(UrlMappingServiceImpl.class);
    private final UrlMappingRepository urlMappingRepository;
    private final UserRepository userRepository;
    private final ClickEventRepository clickEventRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_URL_LENGTH = 6;

    public UrlMappingServiceImpl(UrlMappingRepository urlMappingRepository, UserRepository userRepository, ClickEventRepository clickEventRepository) {
        this.urlMappingRepository = urlMappingRepository;
        this.userRepository = userRepository;
        this.clickEventRepository = clickEventRepository;
    }

    @Override
    public UrlMappingResponse createShortUrl(String originalUrl, UserResponseDTO userResponseDTO) {
        /*
        1. Check if the provided url for shortening is valid, if not throw exception
        2. Check whether user exists in system via email (unique)
        3. Create object of UrlMappingDTO for mapper and pass the data to it (originalURL)
        4. Generate shortUrl (check for duplicates) if not generate it.
        5. Map the entity to UrlMapper
        6. Save to repository
        7. Return the response via UrlMapper
         */

        logger.info("Service :: Shorting the given URL: {}", originalUrl);

        if (!isValidUrl(originalUrl)){
            logger.error("Invalid URL provided: {}", originalUrl);
            throw new InvalidURlException("Invalid URL format.");
        }

        User user = userRepository.findByEmail(userResponseDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userResponseDTO.getEmail()));

        UrlMappingDTO urlMappingDTO = new UrlMappingDTO(originalUrl);

        String shortUrl = generateShortUrl();
        while (urlMappingRepository.findByShortUrl(shortUrl).isPresent()){
            logger.warn("Short URL collision detected: {}. Generating a new one.", shortUrl);
            shortUrl = generateShortUrl();
        }

        UrlMapping urlMapping = UrlMapper.toEntity(urlMappingDTO, user, shortUrl);

        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);
        logger.info("Short URL created: {} for original URL: {}", shortUrl, originalUrl);

        return UrlMapper.toResponse(savedUrlMapping);
    }

    @Override
    public Page<UrlMappingResponse> getAllPaginatedUrlsForUser(User user, int pageNo, int pageSize) {
        /*
        1. Create pageable object (pageNo is 1-based from API, but 0-based in Spring Data)
        2. Fetch paginated URLs for the user
        3. Map entities to DTOs
         */

        logger.info("Fetching paginated URLs for user with email: {}", user.getEmail());
        Pageable pageable = PageRequest.of(pageNo -1, pageSize);
        Page<UrlMapping> urlMappingPage = urlMappingRepository.findByUser(user, pageable);
        Page<UrlMappingResponse> responsesPage = urlMappingPage.map(UrlMapper :: toResponse);
        logger.info("Retrieved {} URLs for user with email: {}", responsesPage.getTotalPages(), user.getEmail());
        return responsesPage;
    }

    @Override
    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime startDate, LocalDateTime endDate) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new IllegalArgumentException("Mapping not found for shortUrl: " + shortUrl));
        return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, startDate, endDate).stream()
                .collect(Collectors.groupingBy(
                        click -> click.getClickDate().toLocalDate(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new ClickEventDTO(LocalDateTime.from(entry.getKey().atStartOfDay()), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDateTime start, LocalDateTime end) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, start, end);
        return clickEvents.stream()
                .collect(Collectors.groupingBy(
                        click -> click.getClickDate().toLocalDate(),
                        Collectors.counting()
                ));
    }

    private String generateShortUrl() {
        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder("https://");
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            shortUrl.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return shortUrl.toString();
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
