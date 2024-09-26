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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.example.vocabulary.TestHelper.getToken;
import static com.example.vocabulary.TestHelper.headers;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    private String token;

    private final String endpoint = "/api/photo";

    private HttpHeaders authHeader() {
        return headers(new TestHelper.HttpHeader(HttpHeaders.AUTHORIZATION, token));
    }

    @BeforeEach
    void token() {
        token = getToken(jwtProvider);
    }


    @ParameterizedTest
    @CsvSource({"/profiles/admin@gmail.com.png"})
    void get_photo_test(String url) throws Exception {
        var bytes = Files.readAllBytes(ResourceUtils.getFile("classpath:static/photo%s".formatted(url)).toPath());
        mockMvc.perform(get(endpoint + url).headers(authHeader()))
                .andExpect(MockMvcResultMatchers.content().bytes(bytes));
    }

    @ParameterizedTest
    @CsvSource({"/profiles/memeber@gmail.com.png"})
    void throw_exception(String url) throws Exception {
        mockMvc.perform(get(endpoint + url).headers(authHeader()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("class path resource [static/photo%s] cannot be resolved to absolute file path because it does not exist".formatted(url))));
    }
}