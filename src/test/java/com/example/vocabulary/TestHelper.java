package com.example.vocabulary;

import com.example.vocabulary.security.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHelper {

    public static String getToken(JwtProvider jwtProvider){
        return jwtProvider.generate(UsernamePasswordAuthenticationToken.authenticated("member@gmail.com", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }
    public static String toJSON(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    public static HttpHeaders headers(HttpHeader ... httpHeaders){

        Map<String, List<String>> headers = new HashMap<>();
        for(var httpHeader : httpHeaders){
            headers.put(httpHeader.header, List.of(httpHeader.value));
        }

        return new HttpHeaders(new MultiValueMapAdapter<>(headers));
    }

    public record HttpHeader(String header, String value){}



}
