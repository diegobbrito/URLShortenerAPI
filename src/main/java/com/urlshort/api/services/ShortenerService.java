package com.urlshort.api.services;

import com.urlshort.api.dto.ShortUrlResponse;
import com.urlshort.api.models.ShortUrl;
import com.urlshort.api.repositories.ShortUrlRepository;
import java.net.URI;
import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShortenerService {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortUrlRepository shortUrlRepository;
    private final String baseUrl;

    public ShortenerService(
            ShortUrlRepository shortUrlRepository,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.shortUrlRepository = shortUrlRepository;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public ShortUrlResponse createShortUrl(String originalUrl) {
        validateUrl(originalUrl);

        String shortenedId;
        while (true) {
            shortenedId = generateShortenedId();
            if (shortUrlRepository.existsById(shortenedId)) {
                continue;
            }

            try {
                shortUrlRepository.save(new ShortUrl(shortenedId, originalUrl));
                return new ShortUrlResponse(shortenedId, originalUrl, baseUrl + "/" + shortenedId);
            } catch (DataIntegrityViolationException exception) {
                if (!shortUrlRepository.existsById(shortenedId)) {
                    throw exception;
                }
            }
        }
    }

    public String getOriginalUrl(String shortenedId) {
        return findShortUrl(shortenedId).getOriginalUrl();
    }

    public void deleteShortUrl(String shortenedId) {
        shortUrlRepository.delete(findShortUrl(shortenedId));
    }

    private ShortUrl findShortUrl(String shortenedId) {
        return shortUrlRepository.findById(shortenedId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "URL encurtada não encontrada"));
    }

    private void validateUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A URL é obrigatória");
        }

        try {
            URI uri = URI.create(originalUrl);
            String scheme = uri.getScheme();
            if (!uri.isAbsolute()
                    || uri.getHost() == null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Informe uma URL absoluta com esquema HTTP ou HTTPS");
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A URL informada é inválida", exception);
        }
    }

    private String generateShortenedId() {
        StringBuilder shortenedId = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            shortenedId.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return shortenedId.toString();
    }
}
