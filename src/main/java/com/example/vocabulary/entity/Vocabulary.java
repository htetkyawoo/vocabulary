package com.example.vocabulary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Please enter spelling.")
    @NotEmpty(message = "Please enter spelling.")
    @NotBlank(message = "Please enter spelling.")
    private String spelling;

    @OneToMany(mappedBy = "vocabulary", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private List<Definition> def = new ArrayList<>();

    @ManyToMany
    private List<Type> types = new ArrayList<>();

    @OneToMany
    private List<Vocabulary> others = new ArrayList<>();

    @OneToOne(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Photo photo;

    public Vocabulary(String spelling) {
        this.spelling = spelling;
    }

    public void addDefinition(Definition definition){
        this.def.add(definition);
        definition.setVocabulary(this);
    }

    public void removeDefinition(Definition definition){
        this.def.remove(definition);
        definition.setVocabulary(this);
    }

    public void addOther(Vocabulary vocabulary){
        this.others.add(vocabulary);
    }

    public void linkToOther(){
        this.others.forEach(o -> {
            o.others.add(this);
        });
    }


    public void unLinkOther() {
        this.others.forEach(o -> {
            o.others.remove(this);
        });
        this.others = null;
    }

    public void removeOther(Vocabulary vocabulary){
        this.others.remove(vocabulary);
    }

    public void addType(Type type){
        this.types.add(type);
        type.getVocabularies().add(this);
    }
    public void removeType(Type type){
        this.types.remove(type);
        type.getVocabularies().remove(this);
    }

}
