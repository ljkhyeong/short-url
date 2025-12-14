package com.personal.short_url.domain;

import org.springframework.stereotype.Component;

public class Base62Utils {
	private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final int RADIX = 62;

	public static String encodeToKey(long paramId) {
		if (paramId < 0)
			throw new IllegalArgumentException("ID must be positive");
		StringBuilder sb = new StringBuilder();

		long id = paramId;
		while (id > 0) {
			sb.append(BASE62.charAt((int)id % RADIX));
			id /= RADIX;
		}
		return sb.reverse().toString();
	}

	public static long decodeToId(String key) {
		long sum = 0;
		long power = 1;
		for (int i = 0; i < key.length(); i++) {
			sum += BASE62.indexOf(key.charAt(key.length() - 1 - i)) * power;
			power *= RADIX;
		}
		return sum;
	}
}
