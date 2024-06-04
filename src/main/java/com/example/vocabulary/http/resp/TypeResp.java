package com.example.vocabulary.http.resp;

import com.example.vocabulary.entity.Type;
import com.example.vocabulary.entity.Vocabulary;
import jakarta.persistence.ManyToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TypeResp {
    private final int id;
    private final String type;

    private TypeResp(Type type){
        this.id = type.getId();
        this.type = type.getType();
    }

    public static TypeResp getTypeResp(Type type){
        return new TypeResp(type);
    }
}
