package com.example.vocabulary.controller;

import com.example.vocabulary.TestHelper;
import com.example.vocabulary.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.example.vocabulary.TestHelper.headers;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ExceptionHandlersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;
    private String token;

    private HttpHeaders authHeader() {
        return headers(new TestHelper.HttpHeader(HttpHeaders.AUTHORIZATION, token));
    }

    @BeforeEach
    void token() {
        token = jwtProvider.generate(UsernamePasswordAuthenticationToken.authenticated("member@gmail.com", "", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

    }

    @ParameterizedTest
    @CsvSource({"hii@gmail.com", "hee@gmail.com"})
    void Access_dined_handler(String email) throws Exception {
        mockMvc.perform(delete("/api/accounts?id=" + email).headers(authHeader()))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("messages")))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Authorization")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("Access Denied")));
    }

    @ParameterizedTest
    @CsvSource({"hii@gmail.com", "hee@gmail.com"})
    void wrong_token(String email) throws Exception {
        token = token.substring("Bearer ".length());
        mockMvc.perform(delete("/api/accounts?id=" + email).headers(authHeader()))
                .andExpect(status().isForbidden());
    }
}