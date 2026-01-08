package com.personal.short_url.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.short_url.infrastructure.persistence.ShortUrlRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {

	private final ShortUrlRepository shortUrlRepository;

	@Async("taskExecutor")
	@Transactional
	public void recordAndIncreaseViewCount(Long shortUrlId, String userAgent) {
		shortUrlRepository.increaseViewCount(shortUrlId);

		log.info("Async Logging - ID: {}, UA: {}", shortUrlId, userAgent);
	}
}
