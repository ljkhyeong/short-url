package com.personal.short_url.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "short_url", indexes = @Index(name = "idx_original_url", columnList = "originalUrl", unique = true))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortUrl {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 2048)
	private String originalUrl;

	public ShortUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
}
