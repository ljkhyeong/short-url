package com.personal.short_url.integration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import com.personal.short_url.api.dto.CreateShortUrlRequest;
import com.personal.short_url.application.ShortUrlService;
import com.personal.short_url.domain.entity.ShortUrl;
import com.personal.short_url.infrastructure.persistence.ShortUrlRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
public class ShortUrlIntegrationTest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ShortUrlService shortUrlService;
	@Autowired
	ShortUrlRepository shortUrlRepository;

	@Container
	static MySQLContainer mysql = new MySQLContainer("mysql:8.0")
		.withDatabaseName("testDb");

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:alpine")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");

		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", redis::getFirstMappedPort);
	}

	@DisplayName("MySQL 환경에서도 단축 URL 사이클이 정상 동작한다")
	@Test
	void testWithRealDataBase() throws Exception {
		// given
		CreateShortUrlRequest request = new CreateShortUrlRequest("https://naver.com");

		// when
		// then
		mockMvc.perform(post("/api/v1/short-links")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.shortKey").value("1"));
	}

	@DisplayName("캐시 적용 확인: 2번 조회해도 조회수는 2번 증가해야 한다")
	@Test
	void cacheAsideAndViewCountTest() throws Exception {
		// given
		String originalUrl = "https://cache-test.com";
		String shortKey = shortUrlService.generateShortUrl(originalUrl);

		// when
		mockMvc.perform(get("/" + shortKey))
			.andExpect(status().isFound());
		mockMvc.perform(get("/" + shortKey))
			.andExpect(status().isFound());

		// then
		ShortUrl shortUrl = shortUrlRepository.findByOriginalUrl(originalUrl).orElseThrow();
		assertThat(shortUrl.getViewCount()).isEqualTo(2L);

	}

}
