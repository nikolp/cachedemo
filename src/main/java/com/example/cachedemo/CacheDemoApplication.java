package com.example.cachedemo;

import com.example.cachedemo.model.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class CacheDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheDemoApplication.class, args);
	}

//	@Bean
//	CommandLineRunner demoClearCachesOnStartup(CacheUtil cacheUtil) {
//		return args -> {
//			cacheUtil.clearAll();
//		};
//	}

}
