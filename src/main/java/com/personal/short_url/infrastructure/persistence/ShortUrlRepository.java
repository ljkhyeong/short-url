package com.personal.short_url.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.personal.short_url.domain.entity.ShortUrl;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

	Optional<ShortUrl> findByOriginalUrl(String shortKey);

	@Modifying(clearAutomatically = true)
	@Query("update ShortUrl s SET s.viewCount = s.viewCount + 1 where s.id = :id")
	void increaseViewCount(@Param("id") Long id);
}
