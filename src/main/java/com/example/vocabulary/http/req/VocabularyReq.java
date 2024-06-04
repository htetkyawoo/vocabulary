package com.example.vocabulary.http.req;

import com.example.vocabulary.entity.Vocabulary;
import com.example.vocabulary.repo.TypeRepo;
import com.example.vocabulary.service.VocabularyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@Component
public class VocabularyReq {

    private long id;

    @NotNull(message = "Please enter spelling.")
    @NotEmpty(message = "Please enter spelling.")
    @NotBlank(message = "Please enter spelling.")
    private String spelling;
    private List<Integer> types = new ArrayList<>();
    private List<VocabularyReq> others = new ArrayList<>();

    public VocabularyReq(String spelling, List<Integer> types, List<VocabularyReq> others) {
        this.spelling = spelling;
        this.types = types;
        this.others = others;
    }

    public Vocabulary toVocabulary(TypeRepo typeRepo, VocabularyService service){
        others.forEach(v -> v.others = Collections.emptyList());
        var vocabulary = new Vocabulary(spelling);
        var existsType = typeRepo.findAllById(types);
        if(existsType.size() == types.size()){
            vocabulary.getTypes().addAll(existsType);
        }else {
            throw new EntityNotFoundException("The type of %s does not exists.".formatted(spelling));
        }
        others.forEach(v -> {
            var vo = service.findBySpelling(v.spelling);
            if(vo.isPresent()){
                vocabulary.addOther(vo.get());
            }else{
                vocabulary.addOther(v.toVocabulary(typeRepo, service));
            }
        });
        return vocabulary;
    }
}
