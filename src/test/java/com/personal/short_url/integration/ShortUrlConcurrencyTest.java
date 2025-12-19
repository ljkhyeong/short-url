package com.personal.short_url.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.personal.short_url.application.ShortUrlService;
import com.personal.short_url.domain.entity.ShortUrl;
import com.personal.short_url.infrastructure.persistence.ShortUrlRepository;

@SpringBootTest
public class ShortUrlConcurrencyTest {

	@Autowired
	ShortUrlService service;
	@Autowired
	ShortUrlRepository repository;

	@AfterEach
	void tearDown() {
		repository.deleteAll();
	}

	@DisplayName("동시에 100개의 요청이 들어오면 조회수도 100이 증가해야 한다")
	@Test
	void viewCountConcurrency() throws InterruptedException {
		// given
		String originalUrl = "https://concurrency.com";
		String shortKey = service.generateShortUrl(originalUrl);

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					service.getOriginalUrl(shortKey);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// then
		ShortUrl shortUrl = repository.findByOriginalUrl(originalUrl).orElseThrow();

		assertThat(shortUrl.getViewCount()).isEqualTo(100L);
	}
}
