package com.urlshort.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShortenerController {

    @GetMapping("/{shortenedId}")
    public ResponseEntity<String> shortenUrl(@PathVariable String shortenedId) {
        return ResponseEntity.ok("Shorten URL endpoint");
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> createUrl() {
        return ResponseEntity.ok("Create URL endpoint");
    }

    @DeleteMapping("/{shortenedId}")
    public ResponseEntity<String> deleteUrl(@PathVariable String shortenedId) {
        return ResponseEntity.ok("Delete URL endpoint");
    }
}
