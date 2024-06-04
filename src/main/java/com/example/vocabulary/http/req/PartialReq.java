package com.example.vocabulary.http.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PartialReq<T> {
    private final T value;

    private final boolean isAdd;

    @JsonCreator
    public PartialReq(@JsonProperty(required = true) T value,
                      @JsonProperty(required = true)
                      boolean isAdd) {
        this.value = value;
        this.isAdd = isAdd;
    }
}
