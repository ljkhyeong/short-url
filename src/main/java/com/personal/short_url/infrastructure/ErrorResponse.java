package com.personal.short_url.infrastructure;

public record ErrorResponse (
	String ErrorCode,
	String message
){}
