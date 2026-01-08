package com.personal.short_url.application;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.short_url.domain.support.Base62Utils;
import com.personal.short_url.domain.entity.ShortUrl;
import com.personal.short_url.application.exception.NotFoundException;
import com.personal.short_url.infrastructure.persistence.ShortUrlRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {

	private final ShortUrlRepository shortUrlRepository;
	private final ViewCountService viewCountService;
	private final StringRedisTemplate redisTemplate;

	@Transactional
	public String generateShortUrl(String originalUrl) {
		return shortUrlRepository.findByOriginalUrl(originalUrl)
			.map(shortUrl -> Base62Utils.encodeToKey(shortUrl.getId()))
			.orElseGet(() -> {
				ShortUrl saved = shortUrlRepository.save(new ShortUrl(originalUrl));
				return Base62Utils.encodeToKey(saved.getId());
			});
	}

	@Transactional
	public String getOriginalUrl(String shortKey, String userAgent) {
		long id = Base62Utils.decodeToId(shortKey);

		String cacheKey = "url:" + id;
		String cachedOriginalUrl = redisTemplate.opsForValue().get(cacheKey);

		if (cachedOriginalUrl != null) {
			log.info("[Cache Hit] {}", shortKey);
			viewCountService.recordAndIncreaseViewCount(id, userAgent);
			return cachedOriginalUrl;
		}

		log.info("[Cache miss] {}", shortKey);
		ShortUrl shortUrl = shortUrlRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 단축 URL 입니다. key :" + shortKey));

		viewCountService.recordAndIncreaseViewCount(shortUrl.getId(), userAgent);
		String originalUrl = shortUrl.getOriginalUrl();

		redisTemplate.opsForValue().set(cacheKey, originalUrl, Duration.ofMinutes(10));

		return originalUrl;
	}
}
