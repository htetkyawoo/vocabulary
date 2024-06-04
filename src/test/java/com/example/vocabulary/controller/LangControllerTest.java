package com.example.vocabulary.controller;

import com.example.vocabulary.TestHelper;
import com.example.vocabulary.entity.Lang;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;
    private String token;

    private final String endpoint = "/api/langs";

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
    @CsvSource({"1,e,1", "2,m,2", "0,e,1", "0,,4"})
    void test_get_method(int id, String lang, int count) throws Exception {
        String endPoint = endpoint + "?id=" + id;
        endPoint += Objects.nonNull(lang) ? "&lang=" + lang : "";
        mockMvc.perform(get(endPoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(count)));
    }


    @Order(2)
    @ParameterizedTest
    @CsvSource({"ge", "pp", "ja", "du"})
    void should_save(String la) throws Exception {
        var lang = new Lang(la);
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(lang)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasKey("lang")))
                .andExpect(jsonPath("$.lang", equalTo(la)));
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"en", "mm", "jp"})
    void should_not_save_with_business_error(String la) throws Exception {
        var lang = new Lang(la);
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(lang)))
               .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("This language already exists.")));
    }

    @Order(4)
    @Test
    void should_not_save_with_validation_error() throws Exception {
        mockMvc.perform(post(endpoint).header("Authorization", token).headers(auth_and_content()).content(toJSON(new Lang(null))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Validation")));

    }

    @Order(5)
    @ParameterizedTest
    @CsvSource({"2,ee", "2,jyp"})
    void should_update(long id, String la) throws Exception {
        var lang = new Lang(la);
        lang.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(lang)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$", hasKey("lang")))
                .andExpect(jsonPath("$.lang", equalTo(la)));
    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"10,ee", "20,jyp"})
    void should_not_update_with_business_error(long id, String la) throws Exception {
        var lang = new Lang(la);
        lang.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(lang)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("No such Language.")));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"1,", "3,"})
    void should_not_update_with_validation_error(long id,String la) throws Exception {
        var lang = new Lang(la);
        lang.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(lang)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Validation")));
    }

    @Order(8)
    @ParameterizedTest
    @CsvSource({"0,ja", "0,my"})
    void should_not_update_with_no_id(long id,String la) throws Exception {
        var lang = new Lang(la);
        lang.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(lang)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("No such Language.")));
    }

}