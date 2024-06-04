package com.example.vocabulary.controller;

import com.example.vocabulary.entity.Type;
import com.example.vocabulary.http.resp.TypeResp;
import com.example.vocabulary.service.TypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/vocabularies/types")
@Tag(
        name = "Types"
)
public class TypeController {

    private final TypeService typeService;

    @GetMapping
    public List<TypeResp> getAllType(@RequestParam Optional<Integer> id, @RequestParam Optional<String> t){
        return typeService.find(id, t);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<?> postType(@Validated @RequestBody Type type , BindingResult result, HttpServletRequest request) throws URISyntaxException {
        var t = typeService.save(type);
        return ResponseEntity.created((new URI( request.getScheme()+ "://" + request.getLocalName() + ":" + request.getServerPort() + request.getRequestURI() + "?id=" + t.getId()))).body(t);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public TypeResp put(@Validated @RequestBody Type type, BindingResult result){
        return typeService.update(type);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping()
    @SecurityRequirement(
            name = "Bearer Auth"
    )
    public ResponseEntity<?> delete(@RequestParam Integer id){
        var deleted = typeService.delete(id);
        if(deleted){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted.");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such type.");
        }
    }
}
