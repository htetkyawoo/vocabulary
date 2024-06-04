package com.example.vocabulary.http.req;

import com.example.vocabulary.entity.Definition;

import java.util.Optional;

public class VocabularyPartialReq {
    private final String spelling;
    private final PartialReq<Integer> type;
    private final PartialReq<VocabularyReq> other;
    private final PartialReq<DefinitionReq> definition;

    public VocabularyPartialReq(String spelling, PartialReq<Integer> type, PartialReq<VocabularyReq> other, PartialReq<DefinitionReq> definition) {
        this.spelling = spelling;
        this.type = type;
        this.other = other;
        this.definition = definition;
    }

    public Optional<String> spelling(){
        return Optional.ofNullable(spelling);
    }
    public Optional<PartialReq<Integer>> type(){
        return Optional.ofNullable(type);
    }
    public Optional<PartialReq<VocabularyReq>> other(){
        return Optional.ofNullable(other);
    }

    public Optional<PartialReq<DefinitionReq>> definition(){
        return Optional.ofNullable(definition);
    }
}
