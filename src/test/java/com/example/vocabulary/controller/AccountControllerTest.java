package com.example.vocabulary.controller;

import com.bastiaanjansen.otp.TOTPGenerator;
import com.dumbster.smtp.SimpleSmtpServer;
import com.example.vocabulary.entity.Account;
import com.example.vocabulary.security.JwtProvider;
import com.example.vocabulary.service.AccountService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;

import static com.example.vocabulary.TestHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;
    private String token;

    @Autowired
    private TOTPGenerator totpGenerator;

    @Autowired
    private AccountService service;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${profile.default.photo}")
    private String defaultProfile;

    private final String endpoint = "/api/accounts";

    private HttpHeaders authHeader() {
        return headers(new HttpHeader(HttpHeaders.AUTHORIZATION, token));
    }

    private HttpHeaders auth_and_content(){
        return headers(
                new HttpHeader(HttpHeaders.AUTHORIZATION, token),
                new HttpHeader(HttpHeaders.CONTENT_TYPE, "APPLICATION/JSON"));
    }

    private HttpHeaders contentHeader(){
        return headers(new HttpHeader(HttpHeaders.CONTENT_TYPE, "APPLICATION/JSON"));
    }
    @BeforeEach
    void token() {
        token = getToken(jwtProvider);
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com,admin,ADMIN", "member@gmail.com,member,USER"})
    void should_found_by_email(String email, String name, Account.Role role) throws Exception {
        mockMvc.perform(get(endpoint + "?id=" + email).headers(authHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                [
                {
                    "email": "%s",
                    "name":"%s",
                    "role": "%s",
                    "vocabularies": []
                }
                ]
                """.formatted(email, name, role)));
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"hello@gmail.com", "hi@gmail"})
    void should_not_found_by_email(String email) throws Exception {
        mockMvc.perform(get(endpoint + "?id=" + email).headers(authHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"ad,1", "admin,1", "mem,1", "m,2"})
    void should_found_by_name(String name, int count) throws Exception {
        mockMvc.perform(get(endpoint + "?name=" + name).headers(authHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> MockMvcResultMatchers.jsonPath(result.toString(), hasSize(count)));
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource({"justin", "members"})
    void should_not_found_by_name(String name) throws Exception {
        mockMvc.perform(get(endpoint + "?name=" + name).headers(authHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> MockMvcResultMatchers.jsonPath(result.toString(), hasSize(0)));
    }

    @Order(5)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com,m,2", "member@gmail.com,people,1","people@gmail.com,m,2"})
    void should_found_by_email_and_name(String email, String name, int count) throws Exception {
        mockMvc.perform(get(endpoint + "?id=" + email + "&name=" + name).headers(authHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> MockMvcResultMatchers.jsonPath(result.toString(), hasSize(count)));
    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"people@gmail.com,p", "account@gmail.com,people"})
    void should_not_found_by_email_and_name(String email, String name) throws Exception {
        mockMvc.perform(get(endpoint + "?id=" + email+ "&name=" + name).headers(authHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> MockMvcResultMatchers.jsonPath(result.toString(), hasSize(0)));
    }


    @Order(7)
    @ParameterizedTest
    @CsvSource({"hello@gmail.com,hello,hello,MALE", "hi@gmail.com,hi,hi,FEMALE"})
    void should_save(String email, String name, String password, Account.Gender gender) throws Exception {
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content( toJSON(Account.createUser(email, name, password, gender))))
                .andExpect(status().isCreated())
                .andExpect(content().string("Successfully created"));
    }

    @Order(8)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com,hello,hello,This email is used by other user.,MALE", "htet@gmail.com,admin,hi,This name is used by other user.,FEMALE"})
    void should_not_save_with_bad_request(String email, String name, String password, String message, Account.Gender gender) throws Exception {
        mockMvc.perform(post(endpoint).headers(auth_and_content()).content( toJSON(Account.createUser(email, name, password, gender))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo("Business")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", equalTo(message)));
    }

    @Order(9)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com,hee,ADMIN,MALE", "member@gmail.com,hii,USER,FEMALE"})
    void should_update(String email, String name, Account.Role role, Account.Gender gender) throws Exception {
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content( toJSON(Account.user(email, name, null, role, gender))))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                {
                    "email": "%s",
                    "name":"%s",
                    "role": "%s",
                    "vocabularies": [],
                    "profile":{"id":0, "url":"%s"}
                }
                """.formatted(email, name, role, defaultProfile)));
    }

    @Order(10)
    @ParameterizedTest
    @CsvSource({"hii@gmail.com,hi,hi,Business,MALE"})
    void should_not_update_with_bad_request(String email, String name, String password, String type, String gender) throws Exception {
        Account account = Account.createAdmin(email, name, password, Account.Gender.valueOf(gender));
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content( toJSON(account)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo(type)));
    }
    @Order(10)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com,,Validation", "member@gmail.com, ,Validation"})
    void should_not_update_with_validation(String email,String name, String type) throws Exception {
        Account account = new Account();
        account.setEmail(email);
        account.setName(name);
        mockMvc.perform(put(endpoint).headers(auth_and_content()).content( toJSON(account)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo(type)));
    }

    @Order(10)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com", "member@gmail.com"})
    void should_get_reset_code(String email) throws Exception {
        var getCodeURL = endpoint + "/reset";
        try (var damp = SimpleSmtpServer.start(SimpleSmtpServer.DEFAULT_SMTP_PORT)) {
            mockMvc.perform(patch(getCodeURL).headers(contentHeader()).content("""
                {"email":"%s"}
                """.formatted(email)))
                    .andExpect(jsonPath("$", equalTo("Reset Code is sent to %s".formatted(email))));

        }
    }
    @Order(10)
    @ParameterizedTest
    @CsvSource({"hii@gmail.com", "hg@gmail.com"})
    void should_not_get_reset_code_password_test(String email) throws Exception{
        var getCodeURL = endpoint + "/reset";
        try (var damp = SimpleSmtpServer.start(SimpleSmtpServer.DEFAULT_SMTP_PORT)) {
            mockMvc.perform(patch(getCodeURL).headers(contentHeader()).content("""
                            {"email":"%s"}
                            """.formatted(email)))
                    .andExpect(jsonPath("$.type", equalTo("Business")))
                    .andExpect(jsonPath("$.messages[0]", equalTo("There is no account register with %s".formatted(email))));

        }
    }

    @Order(10)
    @Test
    void should_not_get_reset_code_test_with_validation() throws Exception{
        var getCodeURL = endpoint + "/reset";
        try (var damp = SimpleSmtpServer.start(SimpleSmtpServer.DEFAULT_SMTP_PORT)) {
            mockMvc.perform(patch(getCodeURL).headers(contentHeader()).content("""
                            {}
                            """))
                    .andExpect(jsonPath("$.type", equalTo("Validation")));
        }
    }

    @Order(10)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com,hello", "member@gmail.com,meme1"})
    void should_reset_password_test(String email, String password) throws Exception{
        var code = totpGenerator.now();
        var getCodeURL = endpoint + "/reset/password";
        mockMvc.perform(patch(getCodeURL).headers(contentHeader()).content("""
                {"email":"%s","password":"%s","code":"%s"}
                """.formatted(email, password, code)))
                .andExpect(jsonPath("$", equalTo("Password is changed. Please login again.")));
        assertTrue(encoder.matches(password,service.findById(email).getPassword() ));
    }

    @Order(10)
    @ParameterizedTest
    @CsvSource({"admin@gmail.com,hello", "member@gmail.com,meme1"})
    void should_not_reset_password_test(String email, String password) throws Exception{
        var getCodeURL = endpoint + "/reset/password";
        mockMvc.perform(patch(getCodeURL).headers(contentHeader()).content("""
                {"email":"%s","password":"%s","code":"%s"}
                """.formatted(email, password, "123456")))
                .andExpect(jsonPath("$.messages[0]", equalTo("Wrong Code.")))
                .andExpect(jsonPath("$.type", equalTo("Authentication")));
    }

    @Order(10)
    @ParameterizedTest
    @CsvSource({"admin.com,hello", "@gmail.com,meme1"})
    void should_not_reset_password_with_wrong_mail_test(String email, String password) throws Exception{
        var getCodeURL = endpoint + "/reset/password";
        mockMvc.perform(patch(getCodeURL).headers(contentHeader()).content("""
                {"email":"%s","password":"%s","code":"%s"}
                """.formatted(email, password, "123456")))
                .andExpect(jsonPath("$.messages[0]", equalTo("Wrong Code.")))
                .andExpect(jsonPath("$.type", equalTo("Authentication")));
    }

    @Order(10)
    @Test
    void profile_upload_test ()throws Exception {
        var photo = ResourceUtils.getFile("classpath:static/photo/profiles/default_profile.png");
        var file = new MockMultipartFile("file", photo.getName(), MediaType.IMAGE_PNG_VALUE, new FileInputStream(photo));
        var file1 = new MockMultipartFile("id", "admin@gmail.com".getBytes());
        RequestPostProcessor postProcessor = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart("/api/accounts").file(file).file(file1)
                        .with(postProcessor)
                        .headers(authHeader()))
                .andDo(print())
                .andExpect(content().string(equalTo("/profiles/admin@gmail.com.png")));
    }

    @Order(10)
    @Test
    void profile_upload_error_test ()throws Exception {
        var photo = ResourceUtils.getFile("classpath:static/photo/profiles/default_profile.png");
        var file = new MockMultipartFile("file", photo.getName(), MediaType.IMAGE_PNG_VALUE, new FileInputStream(photo));
        var file1 = new MockMultipartFile("id", "admin@gmail.com".getBytes());
        RequestPostProcessor postProcessor = (request) -> {
            request.setMethod("PATCH");
            return request;
        };
        mockMvc.perform(multipart("/api/accounts").file(file).file(file1)
                        .with(postProcessor)
                        .headers(authHeader()))
                .andDo(print());
    }

    @Order(11)
    @ParameterizedTest
    @CsvSource({ "member@gmail.com"})
    void should_deleted(String email) throws Exception {
        mockMvc.perform(delete(endpoint + "?id=" + email).headers(authHeader()))
                .andExpect(status().isNoContent());
    }

    @Order(12)
    @ParameterizedTest
    @CsvSource({"hii@gmail.com", "hee@gmail.com"})
    void should_not_deleted(String email) throws Exception {
        mockMvc.perform(delete(endpoint + "?id=" + email).headers(authHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No Such account."));
    }
}