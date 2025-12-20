package com.personal.short_url.api.error;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse (
	String code,
	String message,
	Map<String, String> validationErrors
){
	public ErrorResponse(String code, String message) {
		this(code, message, Collections.emptyMap());
	}
}
