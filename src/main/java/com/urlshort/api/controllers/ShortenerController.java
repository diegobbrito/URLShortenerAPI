package com.urlshort.api.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class ShortenerController {

    @GetMapping("/{shortenedId}")
    public String shortenUrl(@PathVariable String shortenedId) {
        return "Shorten URL endpoint";
    }

    @PostMapping("/shorten")
    public String createUrl() {
        return "Create URL endpoint";
    }

    @DeleteMapping("/{shortenedId}")
    public String deleteUrl(@PathVariable String shortenedId) {
        return "Delete URL endpoint";
    }
}
