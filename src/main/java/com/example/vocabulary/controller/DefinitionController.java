package com.example.vocabulary.controller;

import com.example.vocabulary.entity.Definition;
import com.example.vocabulary.http.req.DefinitionReq;
import com.example.vocabulary.http.resp.DefinitionResp;
import com.example.vocabulary.service.DefinitionService;
import com.example.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vocabularies/{vocabularyId}/{typeId}/definition")
@Tag(
        name = "Definitions"
)
public class DefinitionController {

    private final DefinitionService service;
    private final VocabularyService vocabularyService;
    @GetMapping
    public List<DefinitionResp> get(@PathVariable long vocabularyId, @PathVariable int typeId, @RequestParam(required = false) long ... langs){
        return service.find(vocabularyId, typeId, langs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<DefinitionResp> post(@Validated @RequestBody DefinitionReq definitionReq, BindingResult result, HttpServletRequest request) throws URISyntaxException {
        var def = service.saveReq(definitionReq, vocabularyService);
        return ResponseEntity.created(new URI( request.getScheme()+ "://" + request.getLocalName() + ":" + request.getServerPort() + request.getRequestURI() + "?lang=en")).body(def);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<DefinitionResp> put(@Validated @RequestBody DefinitionReq definitionReq, BindingResult result) throws BadRequestException {
        return ResponseEntity.accepted().body(service.update(definitionReq));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<?> delete(@RequestParam long langId, @PathVariable long vocabularyId, @PathVariable int typeId){
        var deleted = service.delete(vocabularyId, typeId, langId);
        if(deleted){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body("No such definition.");
    }
}