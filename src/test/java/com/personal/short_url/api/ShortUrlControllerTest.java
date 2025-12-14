package com.personal.short_url.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.personal.short_url.api.dto.CreateShortUrlRequest;
import com.personal.short_url.application.ShortUrlService;
import com.personal.short_url.application.exception.NotFoundException;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ShortUrlController.class)
public class ShortUrlControllerTest {
	@Autowired
	MockMvc mockMvc;
	@MockitoBean
	ShortUrlService shortUrlService;
	@Autowired
	ObjectMapper objectMapper;

	@DisplayName("단축 URL 생성 API: 정상 요청 시 200 OK와 단축 키 반환")
	@Test
	void createShortUrlApi() throws Exception {
		// given
		CreateShortUrlRequest request = new CreateShortUrlRequest("https://naver.com");
		given(shortUrlService.generateShortUrl(anyString())).willReturn("A1b");

		// when
		// then
		mockMvc.perform(post("/api/v1/short-links")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.shortKey").value("A1b"));
	}

	// 유효하지 않은 URL 형식에 대한 400 Bad Request 테스트 추가 요망

	@DisplayName("존재하지 않는 단축키로 리다이렉트 요청 시 404를 반환한다")
	@Test
	void redirect_NotFound() throws Exception {
		given(shortUrlService.getOriginalUrl("NoKey")).willThrow(new NotFoundException("key not found"));

		mockMvc.perform(get("/NoKey"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").exists());
	}
}
