package com.example.vocabulary.http.resp;

import com.example.vocabulary.entity.Definition;
import com.example.vocabulary.entity.Lang;
import com.example.vocabulary.entity.Vocabulary;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
public class DefinitionResp {
    private final String id;
    private final String def;
    private final long vocabularyId;
    private final long langId;
    private final int typeId;

    private DefinitionResp(LocalDateTime id, String def, long vocabularyId, long langId, int typeId) {
        this.id = id.toString();
        this.def = def;
        this.vocabularyId = vocabularyId;
        this.langId = langId;
        this.typeId = typeId;
    }

    public static DefinitionResp toDefinitionResp(Definition definition){
        return new DefinitionResp(
                definition.getId(),
                definition.getDef(),
                definition.getVocabulary().getId(),
                definition.getLang().getId(),
                definition.getType().getId());
    }
}
