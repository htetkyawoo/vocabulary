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
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Please enter type.")
    @NotEmpty(message = "Please enter type.")
    @NotBlank(message = "Please enter type.")
    private String type;

    @ManyToMany(mappedBy = "types")
    private List<Vocabulary> vocabularies = new ArrayList<>();

    public Type(String type) {
        this.type = type;
    }

}
