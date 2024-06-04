package com.example.vocabulary.controller;

import com.example.vocabulary.http.req.VocabularyPartialReq;
import com.example.vocabulary.http.req.VocabularyReq;
import com.example.vocabulary.http.resp.PageableResp;
import com.example.vocabulary.http.resp.VocabularyResp;
import com.example.vocabulary.service.fss.FileStorageService;
import com.example.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vocabularies")
@PropertySource({"classpath:storage.properties"})
@Tag(
        name = "Vocabularies"
)
public class VocabularyController {

    private final FileStorageService fileStorageService;
    private final VocabularyService service;
    @Value("${default.pageSize}")
    private Integer pageSize;
    @Value("${images.storage.location}")
    private String imagesStorage;

    @GetMapping
    public PageableResp<VocabularyResp> get(@RequestParam Optional<Long> id,
                                            @RequestParam Optional<String> q,
                                            @RequestParam Optional<String> t,
                                            @RequestParam Optional<Integer> pageNum,
                                            @RequestParam Optional<Integer> s) {
        s.ifPresent(integer -> this.pageSize = integer);
        return service.find(id, q, t, pageNum, pageSize);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<VocabularyResp> post(@Validated @RequestBody VocabularyReq vocabularyReq, BindingResult result, HttpServletRequest request) throws URISyntaxException {
       var vocabulary = service.saveReq(vocabularyReq);
       return ResponseEntity.created((new URI( request.getScheme()+ "://" + request.getLocalName() + ":" + request.getServerPort() + request.getRequestURI() + "?id=" + vocabulary.getId()))).body(vocabulary);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<?> put(@Validated @RequestBody VocabularyReq vocabulary, BindingResult result) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.update(vocabulary));
    }
    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<VocabularyResp> patch(@RequestParam long id,
                                                @RequestPart(value = "update") Optional<VocabularyPartialReq> partialReq, @RequestPart(value = "image", required = false) MultipartFile file) {
        Optional<String> image = Objects.nonNull(file) ? Optional.of(fileStorageService.save(file, String.valueOf(id), imagesStorage + "/images")) : Optional.empty();
        var vocabularyResp = service.partialUpdate(id, partialReq, image);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(vocabularyResp);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<?> delete(@RequestParam long id) {
        var deleted = service.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body("There is no such vocabulary.");
        }

    }



}
