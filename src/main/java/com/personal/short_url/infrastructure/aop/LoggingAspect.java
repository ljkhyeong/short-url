package com.personal.short_url.infrastructure.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Around("execution(* com.personal.short_url.api..*Controller.*(..))")
	public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();

		log.info("--> Request: {}", joinPoint.getSignature().toShortString());

		try {
			Object proceed = joinPoint.proceed();
			long executionTime = System.currentTimeMillis() - start;

			log.info("<-- Response: {} ({}ms)", joinPoint.getSignature().toShortString(), executionTime);
			return proceed;
		} catch (Exception e) {
			log.error("<X- Exception: {} ({}ms)", e.getMessage(), System.currentTimeMillis() - start);
			throw e;
		}
	}
}
