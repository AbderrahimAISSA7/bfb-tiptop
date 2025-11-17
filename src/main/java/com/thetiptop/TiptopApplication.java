package com.thetiptop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.thetiptop.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class TiptopApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiptopApplication.class, args);
	}

}
