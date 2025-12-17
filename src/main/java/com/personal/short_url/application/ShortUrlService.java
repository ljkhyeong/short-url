package com.personal.short_url.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.short_url.domain.support.Base62Utils;
import com.personal.short_url.domain.entity.ShortUrl;
import com.personal.short_url.application.exception.NotFoundException;
import com.personal.short_url.infrastructure.persistence.ShortUrlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

	private final ShortUrlRepository shortUrlRepository;

	@Transactional
	public String generateShortUrl(String originalUrl) {
		return shortUrlRepository.findByOriginalUrl(originalUrl)
			.map(shortUrl -> Base62Utils.encodeToKey(shortUrl.getId()))
			.orElseGet(() -> {
				ShortUrl saved = shortUrlRepository.save(new ShortUrl(originalUrl));
				return Base62Utils.encodeToKey(saved.getId());
			});
	}

	@Transactional
	public String getOriginalUrl(String shortKey) {
		long id = Base62Utils.decodeToId(shortKey);
		ShortUrl shortUrl = shortUrlRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 단축 URL 입니다. key :" + shortKey));

		shortUrl.increaseViewCount();
		
		return shortUrl.getOriginalUrl();
	}
}
