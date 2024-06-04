package com.example.vocabulary.config;

import com.example.vocabulary.security.AccessDeniedResolver;
import com.example.vocabulary.security.filters.JwtFilter;
import com.example.vocabulary.security.filters.ResetPasswordFilter;
import com.example.vocabulary.service.AccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService manager(PasswordEncoder passwordEncoder) {
        var user = new User("admin", passwordEncoder.encode("admin"), Collections.emptyList());
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   JwtFilter jwtFilter,
                                                   ResetPasswordFilter resetPasswordFilter,
                                                   AccountService accountService,
                                                   AccessDeniedResolver resolver) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests(
                reg -> reg.requestMatchers("/api/login",
                                "/api/vocabularies/**",
                                "/api/langs",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-ui",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-resources",
                                "/swagger-resources/**").permitAll()
                        .requestMatchers("/api/accounts/reset/**").hasAnyRole("ADMIN", "RESET")
                        .requestMatchers("/api/accounts/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(resetPasswordFilter, JwtFilter.class);

        httpSecurity.userDetailsService(accountService);
        httpSecurity.exceptionHandling(c -> c.accessDeniedHandler(resolver));
        return httpSecurity.build();

    }
}
