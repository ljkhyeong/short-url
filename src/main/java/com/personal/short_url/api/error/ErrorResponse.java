package com.personal.short_url.api.error;

public record ErrorResponse (
	String ErrorCode,
	String message
){}
