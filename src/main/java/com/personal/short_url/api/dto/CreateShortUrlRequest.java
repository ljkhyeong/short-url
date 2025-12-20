package com.personal.short_url.api.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateShortUrlRequest (
	@NotBlank(message = "URL은 필수 입력값입니다.")
	@Pattern(
		regexp = "^[^\\s]+$",
		message = "올바른 URL 형식이 아닙니다."
	)
	String url
	) {
	public String getSanitizedUrl() {
		String trimmed = url.trim();
		if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
			return "https://" + trimmed;
		}
		return trimmed;
	}
}

// @URL은 프로토콜 존재 여부에 관대함. @Pattern으로 엄격히?
