package com.example.vocabulary.service;

import com.example.vocabulary.entity.Definition;
import com.example.vocabulary.entity.Vocabulary;
import com.example.vocabulary.http.req.DefinitionReq;
import com.example.vocabulary.http.resp.DefinitionResp;
import com.example.vocabulary.repo.DefinitionRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefinitionService {

    private final DefinitionRepo repo;
    private final LangService langService;
    private final TypeService typeService;

    public List<DefinitionResp> find(long vocabularyId, int typeId, long... langs) {
        Specification<Definition> withSpellingAndId = Specification.allOf(
                (rt, q, cb) -> cb.equal(rt.get("vocabulary").get("id"), vocabularyId),
                (rt, q, cb) -> cb.equal(rt.get("type").get("id"), typeId)
        );
        Specification<Definition> withLangs = null;
        if (langs == null || langs.length == 0) {
            withLangs = (rt, q, cb) -> cb.equal(rt.get("lang").get("id"), 1);
        } else {
            withLangs = (rt, q, cb) -> {
                var in = cb.in(rt.get("lang").get("id"));
                Arrays.stream(langs).forEach(in::value);
                return in;
            };
        }
        return repo.findAll(Specification.allOf(withSpellingAndId, withLangs)).stream().map(DefinitionResp::toDefinitionResp).toList();
    }

    public Definition save(DefinitionReq definitionReq, VocabularyService vocabularyService) {
        var definition = new Definition(definitionReq.def());
        definition.setLang(langService.findById(definitionReq.langId()));
        definition.setVocabulary(vocabularyService.findById(definitionReq.vocabularyId()));
        definition.setType(typeService.findById(definitionReq.typeId()));

        if (repo.existsByVocabularyIdAndLangIdAndTypeId(definitionReq.vocabularyId(), definitionReq.langId(), definitionReq.typeId())) {
            throw new EntityExistsException("This definition already exists.");
        }
        definition.getVocabulary().addDefinition(definition);
        return repo.save(definition);
    }

    public DefinitionResp saveReq(DefinitionReq definitionReq, VocabularyService vocabularyService){
        return DefinitionResp.toDefinitionResp(save(definitionReq, vocabularyService));
    }

    public DefinitionResp update(DefinitionReq definition) {
        var defOpt = repo.findByVocabularyIdAndLangIdAndTypeId(definition.vocabularyId(), definition.langId(), definition.typeId());
        if (defOpt.isPresent()) {
            var exists = defOpt.get();
            exists.setDef(definition.def());
            return DefinitionResp.toDefinitionResp(repo.save(exists));
        }
        throw new EntityNotFoundException("There is no such definition.");
    }

    @Transactional
    public boolean delete(long vocabularyId, int typeId, long langId) {
        Specification<Definition> delete = findByVOCandTYandLA(vocabularyId, typeId, langId);
        var effectedRows = repo.delete(delete);
        return effectedRows > 0;
    }

    private static Specification<Definition> findByVOCandTYandLA(long vocabularyId, int typeId, long langId) {
        return Specification.allOf(
                (rt, q, cb) -> cb.equal(rt.get("vocabulary").get("id"), vocabularyId),
                (rt, q, cb) -> cb.equal(rt.get("type").get("id"), typeId),
                (rt, q, cb) -> cb.equal(rt.get("lang").get("id"), langId)
        );
    }

    public Optional<Definition> find(DefinitionReq definition){
        return repo.findByVocabularyIdAndLangIdAndTypeId(definition.vocabularyId(), definition.langId(), definition.typeId());
    }

}
