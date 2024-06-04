package com.example.vocabulary.controller;

import com.example.vocabulary.TestHelper;
import com.example.vocabulary.entity.*;
import com.example.vocabulary.security.JwtProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static com.example.vocabulary.TestHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;
    
    private String token;

    private final String endpoint = "/api/vocabularies/types";

    private HttpHeaders authHeader() {
        return headers(new TestHelper.HttpHeader(HttpHeaders.AUTHORIZATION, token));
    }

    private HttpHeaders auth_and_content(){
        return headers(
                new TestHelper.HttpHeader(HttpHeaders.AUTHORIZATION, token),
                new TestHelper.HttpHeader(HttpHeaders.CONTENT_TYPE, "APPLICATION/JSON"));
    }

    @BeforeEach
    void token() {
        token = getToken(jwtProvider);
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({"1,v,13", "0,a,31", "0,,53"})
    void test_get_method(int id, String type,int count) throws Exception {
        String endPoint = endpoint + "?id=" + id;
        endPoint += Objects.nonNull(type) ? "&t=" + type : "";
        mockMvc.perform(get(endPoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(count)));
    }


    @Order(2)
    @ParameterizedTest
    @CsvSource({"aback,", "abandon"})
    void should_save(String type) throws Exception {
        var type1 = new Type(type);
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(type1)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo(type)));
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"verb", "adverb"})
    void should_not_save_with_business_error(String type) throws Exception {
        var type1 = new Type(type);
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(type1)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("This type is already exists.")));
    }

    @Order(4)
    @Test
    void should_not_save_with_validation_error() throws Exception {
        var type1 = new Type("");
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(type1)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Validation")));
    }

    @Order(5)
    @ParameterizedTest
    @CsvSource({"1,hello", "2,hi"})
    void should_update(int id,String type) throws Exception {
        var type1 = new Type(type);
        type1.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(type1)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("""
    {"id":%s,"type":"%s"}""".formatted(id , type)));
    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"1200,hello", "300,hi,"})
    void should_not_update_with_business_error(int id,String type) throws Exception {
        var type1 = new Type(type);
        type1.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(type1)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("There is no such type.")));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"1, ", "3,"})
    void should_not_update_with_validation_error(int id,String type) throws Exception {
        var type1 = new Type(type);
        type1.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(type1)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Validation")));
    }

    @Order(8)
    @ParameterizedTest
    @CsvSource({"1", "2"})
    void should_deleted(int id) throws Exception {
        mockMvc.perform(delete(endpoint +"?id=" + id ).headers(authHeader()))
                .andExpect(status().isNoContent());
    }

    @Order(9)
    @ParameterizedTest
    @CsvSource({"100", "200"})
    void should_not_deleted(int id) throws Exception {
        mockMvc.perform(delete(endpoint + "?id=" + id ).headers(authHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("There is no such type."));
    }
}