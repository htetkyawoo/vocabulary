package com.example.vocabulary.service;

import com.example.vocabulary.entity.Lang;
import com.example.vocabulary.repo.LangRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LangService {
    
    private final LangRepo repo;
    public List<Lang> find(Optional<Long> id, Optional<String> lang){
        Specification<Lang> withId = id.filter(i -> i > 0).map(i -> (Specification<Lang>) (rt, q, cb) -> cb.equal(rt.get("id"), i)).orElse(null);
        Specification<Lang> withLang = lang.map(i -> (Specification<Lang>) (rt, q, cb) -> cb.like(rt.get("lang"), "%" +i + "%")).orElse(null);
        return repo.findAll(Specification.anyOf(withId, withLang));
    }

    public Lang save(Lang lang){
        if(find(Optional.empty(), Optional.of(lang.getLang())).isEmpty()){
            return repo.save(lang);
        }else{
            throw new EntityExistsException("This language already exists.");
        }
    }

    public Lang update(Lang lang) {
        var exists = findById(lang.getId());
        exists.setLang(lang.getLang());
        return repo.save(exists);
    }

    public Lang findById(long id) {
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("No such Language."));
    }
}
