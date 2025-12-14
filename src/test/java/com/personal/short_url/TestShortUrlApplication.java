package com.personal.short_url;

import org.springframework.boot.SpringApplication;

public class TestShortUrlApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShortUrlApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
