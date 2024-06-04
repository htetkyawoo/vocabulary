package com.example.vocabulary.http.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.converter.HttpMessageConversionException;

public record DefinitionReq(String def, Long vocabularyId, Long langId, Integer typeId) {
    @JsonCreator
    public DefinitionReq(@JsonProperty(required = true) String def,
                         @JsonProperty(required = true) Long vocabularyId,
                         @JsonProperty(required = true) Long langId,
                         @JsonProperty(required = true) Integer typeId) {
        this.def = def;
        this.vocabularyId = vocabularyId;
        this.langId = langId;
        this.typeId = typeId;
    }
}
