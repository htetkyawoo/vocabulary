package com.example.vocabulary.controller;

import com.example.vocabulary.TestHelper;
import com.example.vocabulary.entity.*;
import com.example.vocabulary.http.req.DefinitionReq;
import com.example.vocabulary.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
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
class DefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtProvider jwtProvider;
    private String token;

    private final String endpoint = "/api/vocabularies/%s/%s/definition";

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
    @CsvSource({"1,3,1 2,2", "2,31,,1", "10,3,1,0"})
    void test_get_method(long vocabularyId,int typeId, String langs,int count) throws Exception {
        String endPoint = "";
        if(Objects.nonNull(langs)){
            langs = langs.replaceAll(" ", ",");
            endPoint = String.format(endpoint + "?langs=%s",vocabularyId,typeId,langs);
        }else{
            endPoint = endpoint.formatted(vocabularyId, typeId);
        }
        mockMvc.perform(get(endPoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(count)));
    }


    @Order(2)
    @ParameterizedTest
    @CsvSource({"1,3,3,warjdalfd", "3,31,4,ksdfadklsv"})
    void should_save(long vocabularyId,int typeId, long lang, String def) throws Exception {
        var endPoint = endpoint.formatted(vocabularyId, typeId);
        var definition = new DefinitionReq(def,vocabularyId, lang, typeId);
        mockMvc.perform(post(endPoint).headers(auth_and_content()).content(toJSON(definition)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vocabularyId", equalTo((int)vocabularyId)))
                .andExpect(jsonPath("$.typeId", equalTo(typeId)))
                .andExpect(jsonPath("$.langId", equalTo((int)lang)))
                .andExpect(jsonPath("$.def", equalTo(def)));
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"2,31,1,warjdalfd", "2,31,2,ksdfadklsv"})
    void should_not_save_with_business_error(long vocabularyId,int typeId, long lang, String def) throws Exception {
        var endPoint = endpoint.formatted(vocabularyId, typeId);
        var definition = new DefinitionReq(def,vocabularyId, lang, typeId);

        mockMvc.perform(post(endPoint).headers(auth_and_content()).content(toJSON(definition)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasKey("type")))
                .andExpect(jsonPath("$.type", equalTo("Business")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]", equalTo("This definition already exists.")));
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource({"1,3,3,", "3,31,4,"})
    void should_not_save_with_validation_error(Long vocabularyId,Integer typeId, Long lang, String def) throws Exception {
        var endPoint = endpoint.formatted(vocabularyId, typeId);
        vocabularyId = vocabularyId > 0 ? vocabularyId : null;
        typeId = typeId > 0 ? typeId : null;
        lang = lang > 0 ? lang : null;
        var definition = new DefinitionReq(def,vocabularyId, lang, typeId);

        mockMvc.perform(post(endPoint).headers(auth_and_content()).content(toJSON(definition)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasKey("type")))
                .andExpect(jsonPath("$.type", equalTo("Validation")));
    }

    @Order(5)
    @ParameterizedTest
    @CsvSource({"1,3,1,hello", "2,31,1,hello"})
    void should_update(long vocabularyId,int typeId, long lang, String def) throws Exception {
        var endPoint = endpoint.formatted(vocabularyId, typeId);
        var definition = new DefinitionReq(def,vocabularyId, lang, typeId);

        mockMvc.perform(put(endPoint).headers(auth_and_content()).content(toJSON(definition)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.vocabularyId", equalTo((int)vocabularyId)))
                .andExpect(jsonPath("$.typeId", equalTo(typeId)))
                .andExpect(jsonPath("$.langId", equalTo((int)lang)))
                .andExpect(jsonPath("$.def", equalTo(def)));
    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"1,3,100,warjdalfd", "3,31,10,ksdfadklsv"})
    void should_not_update_with_business_error(long vocabularyId,int typeId, long lang, String def) throws Exception {
        var endPoint = endpoint.formatted(vocabularyId, typeId);
        var definition = new DefinitionReq(def,vocabularyId, lang, typeId);

        mockMvc.perform(put(endPoint).headers(auth_and_content()).content(toJSON(definition)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasKey("type")))
                .andExpect(jsonPath("$.type", equalTo("Business")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]", equalTo("There is no such definition.")));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"1,3,1,", "3,31,2,"})
    void should_not_update_with_validation_error(Long vocabularyId,Integer typeId, Long lang, String def) throws Exception {
        var endPoint = endpoint.formatted(vocabularyId, typeId);
        vocabularyId = vocabularyId > 0 ? vocabularyId : null;
        typeId = typeId > 0 ? typeId : null;
        lang = lang > 0 ? lang : null;
        var definition = new DefinitionReq(def,vocabularyId, lang, typeId);

        mockMvc.perform(put(endPoint).headers(auth_and_content()).content(toJSON(definition)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasKey("type")))
                .andExpect(jsonPath("$.type", equalTo("Validation")));
     }

    @Order(8)
    @ParameterizedTest
    @CsvSource({"1,3,1", "2,31,2"})
    void should_deleted(long vocabularyId,int typeId, long lang) throws Exception {
        mockMvc.perform(delete(String.format(endpoint + "?langId=" + lang, vocabularyId, typeId)).headers(authHeader()))
                .andExpect(status().isNoContent());
    }

    @Order(9)
    @ParameterizedTest
    @CsvSource({"100,3,4", "200,31,3"})
    void should_not_deleted(long vocabularyId,int typeId, long lang) throws Exception {
        mockMvc.perform(delete(String.format(endpoint + "?langId=" + lang, vocabularyId, typeId)).headers(authHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No such definition."));
    }
}