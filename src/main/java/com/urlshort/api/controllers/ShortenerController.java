package com.urlshort.api.controllers;

import com.urlshort.api.dto.CreateShortUrlRequest;
import com.urlshort.api.dto.ShortUrlResponse;
import com.urlshort.api.services.ShortenerService;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShortenerController {

    private final ShortenerService shortenerService;

    public ShortenerController(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }

    @GetMapping("/{shortenedId}")
    public ResponseEntity<Void> redirect(@PathVariable String shortenedId) {
        String originalUrl = shortenerService.getOriginalUrl(shortenedId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortUrlResponse> createUrl(@RequestBody CreateShortUrlRequest request) {
        ShortUrlResponse response = shortenerService.createShortUrl(request.url());
        return ResponseEntity.created(URI.create(response.shortUrl())).body(response);
    }

    @DeleteMapping("/{shortenedId}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortenedId) {
        shortenerService.deleteShortUrl(shortenedId);
        return ResponseEntity.noContent().build();
    }
}
