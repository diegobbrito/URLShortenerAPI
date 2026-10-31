package com.urlshort.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "short_urls")
public class ShortUrl {

    @Id
    @Column(length = 7, nullable = false)
    private String id;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    protected ShortUrl() {
    }

    public ShortUrl(String id, String originalUrl) {
        this.id = id;
        this.originalUrl = originalUrl;
    }

    public String getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
}
