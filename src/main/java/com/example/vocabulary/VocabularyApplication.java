package com.example.vocabulary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableMethodSecurity(jsr250Enabled = true)
public class VocabularyApplication {

	public static void main(String[] args) {
		SpringApplication.run(VocabularyApplication.class, args);

	}

}
