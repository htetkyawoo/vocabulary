package com.example.vocabulary.http.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LogInReq {
    @JsonProperty(required = true)
    String username;

    @JsonProperty(required = true)
    String password;
}
