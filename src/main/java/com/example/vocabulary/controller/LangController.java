package com.example.vocabulary.controller;

import com.example.vocabulary.entity.Lang;
import com.example.vocabulary.repo.LangRepo;
import com.example.vocabulary.service.LangService;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/langs")
@Tag(
        name = "Languages"
)
public class LangController {

    private final LangService langService;

    @GetMapping
    public List<Lang> get(@RequestParam Optional<Long> id, @RequestParam Optional<String> lang){
        return langService.find(id, lang);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<Lang> post(@Validated @RequestBody Lang lang, BindingResult result, HttpServletRequest request) throws URISyntaxException {
        var la = langService.save(lang);
        return ResponseEntity.created(new URI( request.getScheme()+ "://" + request.getLocalName() + ":" + request.getServerPort() + request.getRequestURI() + "?id=" + la.getId())).body(la);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<Lang> put(@Validated @RequestBody Lang lang, BindingResult result) throws BadRequestException {
        return ResponseEntity.accepted().body(langService.update(lang));
    }
}
