package com.example.vocabulary.controller;

import com.example.vocabulary.TestHelper;
import com.example.vocabulary.http.req.VocabularyReq;
import com.example.vocabulary.security.JwtProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.vocabulary.TestHelper.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class VocabularyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    private String token;
    private final String endpoint = "/api/vocabularies";

    private HttpHeaders authHeader() {
        return headers(new HttpHeader(HttpHeaders.AUTHORIZATION, token));
    }

    private HttpHeaders auth_and_content() {
        return headers(new TestHelper.HttpHeader(HttpHeaders.AUTHORIZATION, token), new TestHelper.HttpHeader(HttpHeaders.CONTENT_TYPE, "APPLICATION/JSON"));
    }

    @BeforeEach
    void token() {
        token = getToken(jwtProvider);
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({"1, , ,1", "0,aba, ,4", "0, ,adverb,1", "0,aba,adverb,1"})
    void test_get_method(long id, String q, String t, int count) throws Exception {

        var endPoint = this.endpoint + "?id=%s".formatted(id);
        endPoint += Objects.isNull(q) ? "" : "&q=%s".formatted(q);
        endPoint += Objects.isNull(t) ? "" : "&t=%s".formatted(t);
        mockMvc.perform(get(endPoint)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.jsonPath("$.total", equalTo(count)));
    }


    @Order(2)
    @ParameterizedTest
    @CsvSource({"abashed", "abate"})
    void should_save(String spelling) throws Exception {
        var vocabulary = new VocabularyReq(spelling, List.of(10), Collections.emptyList());
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).headers(authHeader()).content(toJSON(vocabulary))).andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("spelling"))).andExpect(MockMvcResultMatchers.jsonPath("$.spelling", equalTo(spelling)));
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"aback", "abacus"})
    void should_not_save_with_business_error(String spelling) throws Exception {
        var vocabulary = new VocabularyReq(spelling, List.of(10), Collections.emptyList());
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(vocabulary))).andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type"))).andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business"))).andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("This vocabulary is already exists.")));
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"h1", "h2"})
    void should_not_save_with_business_error_due_to_type_not_exists(String spelling) throws Exception {
        var vocabulary = new VocabularyReq(spelling, List.of(100), Collections.emptyList());
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(vocabulary))).andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type"))).andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business"))).andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("The type of %s does not exists.".formatted(spelling))));
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"hello,hi"})
    void nested_save(String spelling1, String spelling2) throws Exception {
        var types = new ArrayList<Integer>();
        types.add(11);
        types.add(10);
        var vocabularyReq2 = new VocabularyReq(spelling2, types, Collections.emptyList());
        var vocabularyReq1 = new VocabularyReq(spelling1, types, List.of(vocabularyReq2));

        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(vocabularyReq1))).andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("spelling"))).andExpect(MockMvcResultMatchers.jsonPath("$.spelling", equalTo(spelling1))).andExpect(MockMvcResultMatchers.jsonPath("$.others", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$.others[0].spelling", equalTo(spelling2))).andExpect(MockMvcResultMatchers.jsonPath("$.types", hasSize(types.size()))).andExpect(MockMvcResultMatchers.jsonPath("$.others[0].types", hasSize(types.size())));

    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"hee,aback,abandoned"})
    void three_nested_save(String spelling1, String spelling2, String spelling3) throws Exception {
        var types = new ArrayList<Integer>();
        types.add(11);
        types.add(10);
        var vocabularyReq3 = new VocabularyReq(spelling3, types, Collections.emptyList());
        var vocabularyReq2 = new VocabularyReq(spelling2, types, List.of(vocabularyReq3));
        var vocabularyReq1 = new VocabularyReq(spelling1, types, List.of(vocabularyReq2));
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(vocabularyReq1))).andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("spelling"))).andExpect(MockMvcResultMatchers.jsonPath("$.spelling", equalTo(spelling1))).andExpect(MockMvcResultMatchers.jsonPath("$.others", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$.others[0].spelling", equalTo(spelling2))).andExpect(MockMvcResultMatchers.jsonPath("$.types", hasSize(types.size()))).andExpect(MockMvcResultMatchers.jsonPath("$.others[0].others", hasSize(0)));
    }

    @Order(4)
    @Test
    void should_not_save_with_validation_error() throws Exception {
        var vocabulary = new VocabularyReq(null, List.of(10), Collections.emptyList());

        mockMvc.perform(post(endpoint).headers(auth_and_content()).content(toJSON(vocabulary))).andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type"))).andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Validation")));
    }

    @Order(5)
    @ParameterizedTest
    @CsvSource({"1,hell", "2,hill"})
    void should_update(long id, String spelling) throws Exception {
        var vocabulary = new VocabularyReq(spelling, List.of(10), Collections.emptyList());
        vocabulary.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(vocabulary))).andExpect(status().isAccepted()).andExpect(jsonPath("$.id", equalTo((int) id))).andExpect(jsonPath("$.spelling", equalTo(spelling)));
    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"10,war", "30,hi"})
    void should_not_update_with_business_error(long id, String spelling) throws Exception {

        var vocabulary = new VocabularyReq(spelling, List.of(10), Collections.emptyList());
        vocabulary.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(vocabulary))).andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type"))).andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business"))).andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo("There is no vocabulary like this.")));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"1, ", "3,"})
    void should_not_update_with_validation_error(long id, String spelling) throws Exception {

        var vocabulary = new VocabularyReq(spelling, List.of(10), Collections.emptyList());
        vocabulary.setId(id);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content(toJSON(vocabulary))).andExpect(status().isBadRequest()).andExpect(jsonPath("$", hasKey("type"))).andExpect(jsonPath("$.type", equalTo("Validation")));
    }


    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,myanmar"})
    void should_patch_spelling_test(long id, String spelling) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"spelling":"%s"}
                """.formatted(spelling).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpect(jsonPath("$.spelling", equalTo(spelling)));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,hello"})
    void should_not_patch_spelling_exists(long id, String spelling) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"spelling":"%s"}
                """.formatted(spelling).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpect(jsonPath("$.type", equalTo("Business"))).andExpect(jsonPath("$.messages[0]", equalTo("%s is already exists".formatted(spelling))));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,4,true", "2,4,false"})
    void should_patch_type_test(long id, int type, boolean isAdd) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        var matcher = isAdd ? hasItem(type) : not(hasItem(type));
        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"type":{"value":"%s", "isAdd":%s}}
                """.formatted(type, isAdd).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpect(jsonPath("$.types", matcher));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,100,true", "2,120,false"})
    void should_not_patch_type_test(long id, int type, boolean isAdd) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"type":{"value":"%s", "isAdd":%s}}
                """.formatted(type, isAdd).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpect(jsonPath("$.type", equalTo("Business"))).andExpect(jsonPath("$.messages[0]", equalTo("There is no such type.")));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,abashed,true", "2,abashed,false"})
    void should_patch_exists_other_test(long id, String spelling, boolean isAdd) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        var match = isAdd ? hasItem(spelling) : not(hasItem(spelling));
        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"other":{"value":{"spelling":"%s"}, "isAdd":%s}}
                """.formatted(spelling, isAdd).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpect(jsonPath("$.others..spelling", match));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,pea,true", "2,pea,false"})
    void should_patch_not_exists_other_test(long id, String spelling, boolean isAdd) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        var match = isAdd ? hasItem(spelling) : not(hasItem(spelling));
        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"other":{"value":{"spelling":"%s"}, "isAdd":%s}}
                """.formatted(spelling, isAdd).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpect(jsonPath("$.others..spelling", match));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,hell,3,3,true", "2,hell,3,3,false", "3,hell,31,4,true"})
    void should_patch_definition_test(long id, String def, int typeId, long langId, boolean isAdd) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        List<ResultMatcher> matchers = new ArrayList<>();
        if (isAdd) {
            matchers.add(jsonPath("$.def..def", hasItem(def)));
            matchers.add(jsonPath("$.def..vocabularyId", hasItem((int) id)));
            matchers.add(jsonPath("$.def..typeId", hasItem(typeId)));
            matchers.add(jsonPath("$.def..langId", hasItem((int) langId)));
        } else {
            matchers.add(jsonPath("$.def..def", not(hasItem(def))));
        }

        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"definition":{"value":{"def":"%s","vocabularyId":%s,"typeId":%s,"langId":%s}, "isAdd":%s}}
                """.formatted(def, id, typeId, langId, isAdd).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpectAll(matchers.toArray(new ResultMatcher[]{}));
    }

    @Order(7)
    @ParameterizedTest
    @CsvSource({"2,hell,3,0,true", "2,,3,3,true"})
    void should_not_patch_definition_test(long id, String def, Long typeId, Long langId, boolean isAdd) throws Exception {
        var endPoint = endpoint + "?id=" + id;
        def = Objects.nonNull(def) ? "\"%s\"".formatted(def) : null;
        MockMultipartFile file1 = new MockMultipartFile("update", "", "application/json", """
                {"definition":{"value":{"def":%s,"vocabularyId":%s,"typeId":%s,"langId":%s}, "isAdd":%s}}
                """.formatted(def, id, typeId, langId, isAdd).getBytes());
        RequestPostProcessor postProcess = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart(endPoint).file(file1).with(postProcess).headers(authHeader())).andExpect(jsonPath("$.type", equalTo("Validation")));
    }


    @Order(8)
    @ParameterizedTest
    @CsvSource({"1", "4"})
    void should_deleted(long id) throws Exception {
        mockMvc.perform(delete(endpoint + "?id=" + id).headers(authHeader())).andExpect(status().isNoContent());
    }

    @Order(9)
    @ParameterizedTest
    @CsvSource({"101", "100"})
    void should_not_deleted(long id) throws Exception {
        mockMvc.perform(delete(endpoint + "?id=" + id).headers(authHeader())).andExpect(status().isBadRequest()).andExpect(content().string("There is no such vocabulary."));
    }
}