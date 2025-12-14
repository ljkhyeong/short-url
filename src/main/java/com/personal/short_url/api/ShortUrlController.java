package com.personal.short_url.api;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.personal.short_url.application.ShortUrlService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShortUrlController {

	private final ShortUrlService shortUrlService;

	@PostMapping("/api/v1/short-links")
	public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request) {
		String shortKey = shortUrlService.generateShortUrl(request.url());

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
