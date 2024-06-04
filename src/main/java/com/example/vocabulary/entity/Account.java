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

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Account {

    @Id
    private String email;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Please enter your name.", groups = {AccountWithoutPassword.class, AccountWithPassword.class})
    @NotEmpty(message = "Please enter your name.", groups = {AccountWithoutPassword.class, AccountWithPassword.class})
    @NotBlank(message = "Please enter your name.", groups = {AccountWithoutPassword.class, AccountWithPassword.class})
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Please enter password.", groups = {AccountWithPassword.class})
    @NotEmpty(message = "Please enter password." , groups = {AccountWithPassword.class})
    @NotBlank(message = "Please enter password.", groups = {AccountWithPassword.class})
    private String password;

    @Column(nullable = false)
    @NotNull(message = "Please select your gender.", groups = {AccountWithPassword.class, AccountWithoutPassword.class})
    private Gender gender;

    @Column(nullable = false)
    private Role role;

    @ManyToMany
    private List<Vocabulary> vocabularies = new ArrayList<>();

    @OneToOne(orphanRemoval = true, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Photo profile;


    public enum Role{
        ADMIN, USER
    }

    public enum Gender{
        MALE, FEMALE
    }

    private Account(String email, String name, String password, Role role, Gender gender) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.gender = gender;
    }

    public static Account createAdmin(String email, String name, String password, Gender gender){
        return new Account(email, name, password, Role.ADMIN, gender);
    }

    public static Account createUser(String email, String name, String password, Gender gender){
        return new Account(email, name, password, Role.USER, gender);
    }
    public static Account user(String email, String name, String password, Role role, Gender gender){
        return new Account(email, name, password, role, gender);
    }

    public interface AccountWithPassword{}
    public interface AccountWithoutPassword{}
}
