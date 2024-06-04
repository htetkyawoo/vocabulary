package com.example.vocabulary.controller;

import com.example.vocabulary.exception.FileUploadException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api/photo")
@Tag(
        name = "Photos"
)
public class PhotoController {

    @Value("${images.storage.location}")
    private String storage;

    @GetMapping(value = "{*path}",produces = {MediaType.IMAGE_PNG_VALUE})
     byte[] get(@PathVariable String path){
        try (var fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:%s%s".formatted(storage, path)))){
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            throw new FileUploadException(e.getMessage());
        }
    }
}
