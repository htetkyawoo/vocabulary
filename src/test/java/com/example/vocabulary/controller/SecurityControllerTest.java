package com.example.vocabulary.controller;

import com.example.vocabulary.TestHelper;
import com.example.vocabulary.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static com.example.vocabulary.TestHelper.headers;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(DataSourceAutoConfiguration.class)
class SecurityControllerTest {

    private final String endPoint = "/api/login";

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders contentHeader() {
        return headers(
                new TestHelper.HttpHeader(HttpHeaders.CONTENT_TYPE, "APPLICATION/JSON"));
    }


    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post(endPoint).content("""
                        {"username":"admin@gmail.com","password":"admin"}""").headers(contentHeader()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasKey("token")))
                .andExpect(jsonPath("$.token", startsWith("Bearer ")));
    }

    @Test
    void login_with_name() throws Exception {
        mockMvc.perform(post(endPoint).content("""
                        {"username":"admin","password":"admin"}""").headers(contentHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("token")))
                .andExpect(jsonPath("$.token", startsWith("Bearer ")));
    }

    @Test
    void throw_back_credential_exception() throws Exception {
        mockMvc.perform(post(endPoint).content("""
                        {"username":"admin@gmail.com","password":"admi"}""").headers(contentHeader()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", hasKey("type")))
                .andExpect(jsonPath("$.type", equalTo("Authentication")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]", equalTo("Username or Password is wrong.")));
    }

    @Test
    void throw_username_not_found_exception() throws Exception {
        mockMvc.perform(post(endPoint).content("""
                        {"username":"admin@admin","password":"admi"}""").headers(contentHeader()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", hasKey("type")))
                .andExpect(jsonPath("$.type", equalTo("Authentication")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]", equalTo("Username or Password is wrong.")));
    }

    @Test
    void unauthorized_exception() {
        var a = new AnonymousAuthenticationToken("admin", "admin", List.of(new SimpleGrantedAuthority("ADMIN")));
        var exception = assertThrows(AccessDeniedException.class, () -> {
            jwtProvider.generate(a);
        });
        assertEquals("Authentication Error", exception.getMessage());
    }

}
