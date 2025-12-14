package com.personal.short_url.domain;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Base62UtilsTest {

	@DisplayName("10진수 ID를 Base62 문자열로 변환하고, 다시 복원할 수 있어야 한다.")
	@Test
	void encodingAndDecoding() {
		// given
		long originalId = 123456789L;

		// when
		String shortKey = Base62Utils.encodeToKey(originalId);
		long decodedId = Base62Utils.decodeToId(shortKey);

		// then
		assertThat(decodedId).isEqualTo(originalId);
		assertThat(shortKey).isNotBlank();
		System.out.println("Encoded : " + shortKey);
	}

	// 0이나 음수에 대한 예외 처리 테스트 추가?

}
