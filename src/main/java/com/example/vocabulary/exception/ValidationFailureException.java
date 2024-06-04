package com.example.vocabulary.exception;

import lombok.Getter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;

@Getter
public class ValidationFailureException extends RuntimeException{

    private final List<String> messages;

    public ValidationFailureException(BindingResult result) {
        messages = result.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
    }

}
