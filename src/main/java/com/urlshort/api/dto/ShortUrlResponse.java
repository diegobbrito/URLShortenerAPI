package com.urlshort.api.dto;

public record ShortUrlResponse(String id, String originalUrl, String shortUrl) {
}
