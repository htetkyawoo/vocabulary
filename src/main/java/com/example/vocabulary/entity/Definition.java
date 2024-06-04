package com.example.vocabulary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Definition {

    @Id
    private LocalDateTime id;

    @NotNull(message = "Please enter definition.")
    @NotEmpty(message = "Please enter definition.")
    @NotBlank(message = "Please enter definition.")
    private String def;

    @ManyToOne
    private Vocabulary vocabulary;
    @ManyToOne
    private Lang lang;
    @ManyToOne
    private Type type;

    public Definition(String def) {
        setIdNow();
        this.def = def;
    }

    public void setIdNow() {
        this.id = LocalDateTime.now();
    }
}
