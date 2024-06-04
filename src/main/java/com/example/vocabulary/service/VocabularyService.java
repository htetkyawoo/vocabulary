package com.example.vocabulary.service;

import com.example.vocabulary.entity.Photo;
import com.example.vocabulary.entity.Vocabulary;
import com.example.vocabulary.http.req.VocabularyPartialReq;
import com.example.vocabulary.http.req.VocabularyReq;
import com.example.vocabulary.http.resp.PageableResp;
import com.example.vocabulary.http.resp.VocabularyResp;
import com.example.vocabulary.repo.TypeRepo;
import com.example.vocabulary.repo.VocabularyRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepo repo;
    private final TypeRepo typeRepo;
    private final DefinitionService definitionService;

    public PageableResp<VocabularyResp> find(@RequestParam Optional<Long> id,
                                             @RequestParam Optional<String> q,
                                             @RequestParam Optional<String> t,
                                             @RequestParam Optional<Integer> pageNum,
                                             int pageSize) {
        return pageNum.map(integer -> VocabularyResp.toPageableResp(repo.findAll(findSpecifications(id, q, t), PageRequest.of(integer, pageSize))))
                .orElseGet(() -> VocabularyResp.toPageableResp(repo.findAll(findSpecifications(id, q, t), Pageable.ofSize(pageSize))));
    }

    public Optional<Vocabulary> findBySpelling(String spelling) {
        return repo.findBySpelling(spelling);
    }

    public Vocabulary findById(long id) {
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("There is no such vocabulary."));
    }

    private Specification<Vocabulary> findSpecifications(Optional<Long> id, Optional<String> q, Optional<String> t) {
        Specification<Vocabulary> withId = id.filter(i -> i > 0).map(i -> (Specification<Vocabulary>) (root, query, cb) -> cb.equal(root.get("id"), i)).orElse(null);
        Specification<Vocabulary> withSpelling = q.map(i -> (Specification<Vocabulary>) (root, query, cb) -> cb.like(root.get("spelling"), "%" + i + "%")).orElse(null);
        Specification<Vocabulary> withTypes = t.map(i -> (Specification<Vocabulary>) (root, query, cb) -> cb.like(root.get("types").get("type"), "%" + i + "%")).orElse(null);
        return Specification.allOf(withTypes, withSpelling, withId);
    }

    private boolean exists(VocabularyReq req) {
        Specification<Vocabulary> spelling = ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("spelling"), req.getSpelling()));
        List<Specification<Vocabulary>> types = new ArrayList<>();
        req.getTypes().forEach(t ->
                types.add((rt, q, cb) -> cb.like(rt.get("types").get("type"), "%" + t + "%")));
        return !repo.findAll(Specification.anyOf(Specification.anyOf(types), spelling)).isEmpty();
    }

    public Vocabulary save(VocabularyReq vocabularyReq) {
        if (exists(vocabularyReq)) {
            throw new EntityExistsException("This vocabulary is already exists.");
        }
        var vocabulary = vocabularyReq.toVocabulary(typeRepo, this);
        repo.saveAll(vocabulary.getOthers());
        vocabulary = repo.save(vocabulary);
        vocabulary.linkToOther();
        vocabulary = repo.saveAndFlush(vocabulary);
        return vocabulary;
    }

    public VocabularyResp saveReq(VocabularyReq vocabularyReq) {
        return VocabularyResp.toVocabularyResp(save(vocabularyReq), null);
    }

    public VocabularyResp update(VocabularyReq vocabulary) {
        var exVocabulary = repo.findById(vocabulary.getId());
        if (exVocabulary.isPresent()) {
            checkSpellingExists(vocabulary.getSpelling());
            var exists = exVocabulary.get();
            exists.setSpelling(vocabulary.getSpelling());
            return VocabularyResp.toVocabularyResp(repo.save(exists), null);
        } else {
            throw new EntityNotFoundException("There is no vocabulary like this.");
        }
    }

    private void checkSpellingExists(String spelling) {
        if (repo.findBySpelling(spelling).isPresent()) {
            throw new EntityExistsException("%s is already exists".formatted(spelling));
        }
    }

    public VocabularyResp partialUpdate(long id,
                                        Optional<VocabularyPartialReq> req,
                                        Optional<String> image) {
        var vocabularyOpt = repo.findById(id);
        return vocabularyOpt.map(v -> {
            req.ifPresent(vocreq -> {
                vocreq.spelling().ifPresent(s -> {
                    checkSpellingExists(s);
                    v.setSpelling(s);
                });
                vocreq.type().ifPresent(i -> typeRepo.findById(i.getValue()).ifPresentOrElse(t -> {
                    if (i.isAdd()) {
                        v.addType(t);
                    } else {
                        v.removeType(t);
                    }
                }, () -> {
                    throw new EntityNotFoundException("There is no such type.");
                }));
                vocreq.other().ifPresent(i -> {
                    var otherOpt = repo.findBySpelling(i.getValue().getSpelling());
                    if (otherOpt.isPresent()) {
                        var t = otherOpt.get();
                        if (i.isAdd()) {
                            v.addOther(t);
                        } else {
                            v.removeOther(t);
                        }
                    } else {
                        if (i.isAdd()) {
                            var added = save(i.getValue());
                            v.addOther(added);
                        }
                    }
                });
                vocreq.definition().ifPresent(i -> {
                    var value = i.getValue();
                    var defOpt = definitionService.find(value);
                    if (defOpt.isPresent()) {
                        var t = defOpt.get();
                        if (i.isAdd()) {
                            definitionService.update(value);
                        } else {
                            v.removeDefinition(t);
                        }
                    } else {
                        if (i.isAdd()) {
                            var def = definitionService.save(value, this);
                            v.addDefinition(def);
                        }
                    }
                });
            });
            image.ifPresent(img -> v.setPhoto(new Photo(img)));
            return VocabularyResp.toVocabularyResp(repo.save(v), null);
        }).orElseThrow(() -> new EntityNotFoundException("There is no vocabulary like this."));
    }

    public boolean delete(long id) {
        var vo = repo.findById(id);
        if (vo.isPresent()) {
            var v = vo.get();
            v.unLinkOther();
            repo.saveAndFlush(v);
            repo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
