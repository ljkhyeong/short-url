package com.personal.short_url.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.short_url.infrastructure.persistence.ShortUrlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewCountService {

	private final ShortUrlRepository shortUrlRepository;

	@Async
	@Transactional
	public void increaseViewCount(Long shortUrlId) {
		shortUrlRepository.increaseViewCount(shortUrlId);
	}
}
