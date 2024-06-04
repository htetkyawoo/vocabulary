package com.example.vocabulary.controller;

import com.example.vocabulary.exception.FileUploadException;
import com.example.vocabulary.exception.ValidationFailureException;
import com.example.vocabulary.http.resp.ErrorResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jdk.jfr.ContentType;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler({PersistenceException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult entity(PersistenceException e) {
            return new ErrorResult(ErrorResult.Type.Business, List.of(e.getMessage()));
    }

    @ExceptionHandler({FileUploadException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult entity(FileUploadException e) {
            return new ErrorResult(ErrorResult.Type.Business, List.of(e.getMessage()));
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResult handle(AccessDeniedException e) {
        return new ErrorResult(ErrorResult.Type.Authorization, List.of(e.getMessage()));
    }

    @ExceptionHandler({ValidationFailureException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResult handle(ValidationFailureException e) {
        return new ErrorResult(ErrorResult.Type.Validation, e.getMessages());
    }

    @ExceptionHandler({HttpMessageConversionException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResult handle(HttpMessageConversionException e) {
        return new ErrorResult(ErrorResult.Type.Validation, List.of(e.getMessage()));
    }



    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResult handle(AuthenticationException e) {
        return new ErrorResult(ErrorResult.Type.Authentication,
                List.of(e.getMessage()));
    }
}
