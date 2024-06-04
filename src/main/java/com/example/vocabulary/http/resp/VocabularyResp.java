package com.example.vocabulary.http.resp;

import com.example.vocabulary.entity.Definition;
import com.example.vocabulary.entity.Photo;
import com.example.vocabulary.entity.Vocabulary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VocabularyResp {

    private final long id;
    private final String spelling;
    private final List<DefinitionResp> def;
    private final List<Integer> types = new ArrayList<>();
    private final List<VocabularyResp> others = new ArrayList<>();
    private Photo photo;

    public VocabularyResp(Long id,String spelling, List<DefinitionResp> def) {
        this.id = id;
        this.spelling = spelling;
        this.def = def;
    }

    public static VocabularyResp toVocabularyResp(Vocabulary vocabulary, Vocabulary parent){
        var vocabularyResp = new VocabularyResp(vocabulary.getId(), vocabulary.getSpelling(), vocabulary.getDef().stream().map(DefinitionResp::toDefinitionResp).toList());
        vocabulary.getTypes().forEach(type -> vocabularyResp.types.add(type.getId()));
        vocabulary.getOthers().forEach(vo -> {
            if(!vo.equals(parent)){
                vocabularyResp.others.add(toVocabularyResp(vo, vocabulary));
            }
        });
        vocabularyResp.photo = vocabulary.getPhoto();
        return vocabularyResp;
    }

    public static PageableResp<VocabularyResp> toPageableResp(Page<Vocabulary> page){
        return PageableResp.toPageableResp(page.get().map(vo -> VocabularyResp.toVocabularyResp(vo, null)).toList(), page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.nextOrLastPageable(), page.previousOrFirstPageable());
    }
}
