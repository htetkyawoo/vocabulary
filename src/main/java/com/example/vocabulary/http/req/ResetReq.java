package com.example.vocabulary.http.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ResetReq(String email, String password, String code) {

    @JsonCreator
    public ResetReq(@JsonProperty(required = true) String email, String password, String code) {
        this.email = email;
        this.password = password;
        this.code = code;
    }
}
