package com.example.vocabulary.aop;

import com.example.vocabulary.exception.ValidationFailureException;
import com.example.vocabulary.http.req.DefinitionReq;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;

@Configuration
@Aspect
public class ValidationResultAspects {

    @Before(value = "within(com.example.vocabulary.controller.*) && args(*,result,..)", argNames = "result")
    public void checkResult(BindingResult result){
        if(result.hasFieldErrors()){
            result.getAllErrors().forEach(System.out::println);
            throw new ValidationFailureException(result);
        }
    }

    @Before(value = "within(com.example.vocabulary.service.DefinitionService) && args(definitionReq, ..)", argNames = "definitionReq")
    public void checkDefinition(DefinitionReq definitionReq){
        if(definitionReq.def() == null || definitionReq.vocabularyId() <= 0 || definitionReq.langId() <= 0 || definitionReq.typeId() <=0){
            throw new HttpMessageConversionException("Id must greater than 0");
        }
    }
}
