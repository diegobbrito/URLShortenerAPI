package com.urlshort.api.services;

import com.urlshort.api.dto.ShortUrlResponse;
import java.net.URI;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShortenerService {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ConcurrentMap<String, String> urls = new ConcurrentHashMap<>();
    private final String baseUrl;

    public ShortenerService(@Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public ShortUrlResponse createShortUrl(String originalUrl) {
        validateUrl(originalUrl);

        String shortenedId;
        do {
            shortenedId = generateShortenedId();
        } while (urls.putIfAbsent(shortenedId, originalUrl) != null);

        return new ShortUrlResponse(shortenedId, originalUrl, baseUrl + "/" + shortenedId);
    }

    public String getOriginalUrl(String shortenedId) {
        String originalUrl = urls.get(shortenedId);
        if (originalUrl == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL encurtada não encontrada");
        }
        return originalUrl;
    }

    public void deleteShortUrl(String shortenedId) {
        if (urls.remove(shortenedId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL encurtada não encontrada");
        }
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
