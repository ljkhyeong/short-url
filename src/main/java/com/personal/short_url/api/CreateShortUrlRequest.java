package com.personal.short_url.api;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlRequest (
	@NotBlank(message = "URL은 필수 입력값입니다.")
	@URL(message = "올바른 URL 형식이 아닙니다.")
	String url
	) {}

// @URL은 프로토콜 존재 여부에 관대함. @Pattern으로 엄격히?
