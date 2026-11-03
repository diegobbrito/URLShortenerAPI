package com.urlshort.api.services;

import com.urlshort.api.dto.ShortUrlResponse;
import com.urlshort.api.models.ShortUrl;
import com.urlshort.api.repositories.ShortUrlRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShortenerServiceTest {

    private final ShortUrlRepository repository = mock(ShortUrlRepository.class);
    private final ShortenerService service = new ShortenerService(repository, "https://short.example///");

    @Test
    void createsShortUrlWithSevenCharacterIdAndConfiguredBaseUrl() {
        String originalUrl = "https://example.com/path";
        when(repository.existsById(any())).thenReturn(true, false);

        ShortUrlResponse response = service.createShortUrl(originalUrl);

        assertEquals(originalUrl, response.originalUrl());
        assertTrue(response.id().matches("[0-9a-zA-Z]{7}"));
        assertEquals("https://short.example/" + response.id(), response.shortUrl());
        verify(repository).save(any(ShortUrl.class));
        verify(repository, times(2)).existsById(any());
    }

    @Test
    void retrievesAndDeletesShortUrl() {
        String originalUrl = "http://example.com";
        when(repository.existsById(any())).thenReturn(false);
        ShortUrlResponse response = service.createShortUrl(originalUrl);
        ShortUrl storedUrl = new ShortUrl(response.id(), originalUrl);
        when(repository.findById(response.id()))
                .thenReturn(Optional.of(storedUrl), Optional.of(storedUrl), Optional.empty());

        assertEquals(originalUrl, service.getOriginalUrl(response.id()));

        service.deleteShortUrl(response.id());

        verify(repository).delete(storedUrl);
        assertStatus(HttpStatus.NOT_FOUND, () -> service.getOriginalUrl(response.id()));
    }

    @Test
    void rejectsMissingOrInvalidUrls() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl(null));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("  "));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("not a url"));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("ftp://example.com"));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.createShortUrl("https:/path"));
        verify(repository, never()).save(any(ShortUrl.class));
    }

    @Test
    void returnsNotFoundWhenGettingOrDeletingUnknownId() {
        when(repository.findById("missing")).thenReturn(Optional.empty());
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
