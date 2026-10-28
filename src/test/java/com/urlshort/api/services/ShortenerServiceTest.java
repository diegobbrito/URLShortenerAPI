package com.urlshort.api.services;

import com.urlshort.api.dto.ShortUrlResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortenerServiceTest {

    private final ShortenerService service = new ShortenerService("https://short.example///");

    @Test
    void createsShortUrlWithSevenCharacterIdAndConfiguredBaseUrl() {
        String originalUrl = "https://example.com/path";

        ShortUrlResponse response = service.createShortUrl(originalUrl);

        assertEquals(originalUrl, response.originalUrl());
        assertTrue(response.id().matches("[0-9a-zA-Z]{7}"));
        assertEquals("https://short.example/" + response.id(), response.shortUrl());
    }

    @Test
    void retrievesAndDeletesShortUrl() {
        String originalUrl = "http://example.com";
        ShortUrlResponse response = service.createShortUrl(originalUrl);

        assertEquals(originalUrl, service.getOriginalUrl(response.id()));

        service.deleteShortUrl(response.id());

        assertStatus(HttpStatus.NOT_FOUND, () -> service.getOriginalUrl(response.id()));
    }

    @Test
    void rejectsMissingOrInvalidUrls() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl(null));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("  "));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("not a url"));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("ftp://example.com"));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("https:/path"));
    }

    @Test
    void returnsNotFoundWhenGettingOrDeletingUnknownId() {
        assertStatus(HttpStatus.NOT_FOUND, () -> service.getOriginalUrl("missing"));
        assertStatus(HttpStatus.NOT_FOUND, () -> service.deleteShortUrl("missing"));
    }

    private void assertStatus(HttpStatus expectedStatus, Executable action) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, action::execute);
        assertEquals(expectedStatus, exception.getStatusCode());
    }

    @FunctionalInterface
    private interface Executable {
        void execute();
    }
}
