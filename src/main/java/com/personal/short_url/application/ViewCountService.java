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
	public void increaseViewCount(Long shortUrlId) {
		log.info("비동기 조회수 증가 실행 중... Thread: {}", Thread.currentThread().getName());
		shortUrlRepository.increaseViewCount(shortUrlId);
	}
}
