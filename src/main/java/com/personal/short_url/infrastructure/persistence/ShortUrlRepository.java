package com.personal.short_url.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.short_url.domain.entity.ShortUrl;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

	Optional<ShortUrl> findByOriginalUrl(String shortKey);
}
