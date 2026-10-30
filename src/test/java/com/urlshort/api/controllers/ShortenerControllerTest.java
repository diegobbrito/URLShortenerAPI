package com.urlshort.api.controllers;

import com.urlshort.api.dto.ShortUrlResponse;
import com.urlshort.api.dto.CreateShortUrlRequest;
import com.urlshort.api.services.ShortenerService;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShortenerControllerTest {

    private final ShortenerService shortenerService = mock(ShortenerService.class);
    private final ShortenerController controller = new ShortenerController(shortenerService);

    @Test
    void createsShortUrlAndReturnsCreatedResponse() {
        String originalUrl = "https://example.com/path";
        ShortUrlResponse response = new ShortUrlResponse("abc1234", originalUrl, "https://short.example/abc1234");
        when(shortenerService.createShortUrl(originalUrl)).thenReturn(response);

        ResponseEntity<ShortUrlResponse> result = controller.createUrl(
                new CreateShortUrlRequest(originalUrl));

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create(response.shortUrl()), result.getHeaders().getLocation());
        assertSame(response, result.getBody());
        verify(shortenerService).createShortUrl(originalUrl);
    }

    @Test
    void redirectsToOriginalUrl() {
        String shortenedId = "abc1234";
        String originalUrl = "https://example.com/path";
        when(shortenerService.getOriginalUrl(shortenedId)).thenReturn(originalUrl);

        ResponseEntity<Void> result = controller.redirect(shortenedId);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertEquals(URI.create(originalUrl), result.getHeaders().getLocation());
        verify(shortenerService).getOriginalUrl(shortenedId);
    }

    @Test
    void deletesShortUrlAndReturnsNoContent() {
        String shortenedId = "abc1234";

        ResponseEntity<Void> result = controller.deleteUrl(shortenedId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(shortenerService).deleteShortUrl(shortenedId);
    }
}
