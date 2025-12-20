package com.personal.short_url.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.personal.short_url.domain.support.Base62Utils;
import com.personal.short_url.domain.entity.ShortUrl;
import com.personal.short_url.infrastructure.persistence.ShortUrlRepository;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {
	@Mock
	ShortUrlRepository shortUrlRepository;
	@InjectMocks
	ShortUrlService shortUrlService;

	@DisplayName("URL 단축 요청 시 저장 후 인코딩된 키를 반환한다")
	@Test
	void createShortUrl() {
		// given
		String originalUrl = "https://www.google.com";
		ShortUrl savedEntity = new ShortUrl(originalUrl);
		ReflectionTestUtils.setField(savedEntity, "id", 100L);

		given(shortUrlRepository.save(any())).willReturn(savedEntity);

		// when
		String shortKey = shortUrlService.generateShortUrl(originalUrl);

		// then
		assertThat(shortKey).isNotNull();
		verify(shortUrlRepository).save(any());
	}

	@DisplayName("이미 존재하는 URL이면 기존의 ShortKey를 반환해야 한다")
	@Test
	void createShortUrl_duplicate() {
		// given
		String url = "https://google.com";
		ShortUrl existingEntity = new ShortUrl(url);
		ReflectionTestUtils.setField(existingEntity, "id", 100L);

		given(shortUrlRepository.findByOriginalUrl(url)).willReturn(Optional.of(existingEntity));

		// when
		String shortKey = shortUrlService.generateShortUrl(url);

		// then
		verify(shortUrlRepository, never()).save(any());
		assertThat(shortKey).isEqualTo(Base62Utils.encodeToKey(100L));
	}

	@DisplayName("리다이렉트 시 조회수가 1 증가해야 한다")
	@Test
	void increaseViewCount() {
		// given
		ShortUrl shortUrl = new ShortUrl("https://google.com");
		ReflectionTestUtils.setField(shortUrl, "id", 100L);

		given(shortUrlRepository.findById(anyLong())).willReturn(Optional.of(shortUrl));

		// when
		shortUrlService.getOriginalUrl("1C");

		// then
		// TODO: 현재는 엔티티를 안거치고 동기화를 위해 DB에 직접 쿼리를 날리는 로직이라 추후에 수정 필요할듯
		verify(shortUrlRepository, times(1)).increaseViewCount(100L);
	}
}
