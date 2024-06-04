package com.example.vocabulary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@NoArgsConstructor
public class Photo {

    @Id
    private long id;

    @Column(nullable = false)
    @NotNull(message = "Please enter the url of photo.")
    @NotEmpty(message = "Please enter the url of photo.")
    private String url;

    public Photo(String url){
        this.url = url;
    }

}
