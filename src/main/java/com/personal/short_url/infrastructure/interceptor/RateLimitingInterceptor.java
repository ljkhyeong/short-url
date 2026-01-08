package com.personal.short_url.infrastructure.interceptor;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

	private final StringRedisTemplate redisTemplate;

	// TODO : yml로 빼자.
	private static final int MAX_REQUESTS_PER_MINUTE = 50;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO : 프록시 쓸때 감안하면 X-Forwarded-For 쓰는게 나을지도
		String clientIp = request.getRemoteAddr();
		String key = "rate_limit:" + clientIp;

		Long count = redisTemplate.opsForValue().increment(key);

		// TODO : expire 하기전에 서버가 죽으면 영구키로 저장될듯?
		if (count != null && count == 1) {
			redisTemplate.expire(key, Duration.ofMinutes(1));
		}

		if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
			log.warn("분당 요청 리미트 돌파 for IP : {}", clientIp);
			response.sendError(429, "Too Many Requests");
			return false;
		}
		return true;
	}


}
