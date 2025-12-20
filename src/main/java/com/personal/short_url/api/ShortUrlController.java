package com.personal.short_url.api;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.personal.short_url.api.dto.CreateShortUrlRequest;
import com.personal.short_url.api.dto.CreateShortUrlResponse;
import com.personal.short_url.api.error.ErrorResponse;
import com.personal.short_url.application.ShortUrlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShortUrlController {

	private final ShortUrlService shortUrlService;

	@Operation(summary = "단축 URL 생성", description = "긴 URL을 입력받아 단축 Key를 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 URL 형식",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/api/v1/short-links")
	public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request) {
		String shortKey = shortUrlService.generateShortUrl(request.getSanitizedUrl());

		return ResponseEntity.ok(new CreateShortUrlResponse(shortKey));
	}

	@GetMapping("/{shortKey}")
	public ResponseEntity<Void> redirect(@PathVariable String shortKey) {
		String originalUrl = shortUrlService.getOriginalUrl(shortKey);

		return ResponseEntity
			.status(HttpStatus.FOUND)
			.location(URI.create(originalUrl))
			.build();
	}
}
