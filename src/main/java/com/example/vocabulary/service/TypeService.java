package com.example.vocabulary.service;

import com.example.vocabulary.entity.Type;
import com.example.vocabulary.http.resp.TypeResp;
import com.example.vocabulary.repo.TypeRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TypeService{

    private final TypeRepo repo;

    public List<TypeResp> find(Optional<Integer> id, Optional<String> t){
        Specification<Type> withId = id.filter(i -> i > 0).map(i -> (Specification<Type>) (rt, q, cb) -> cb.equal(rt.get("id"), i)).orElse(null);
        Specification<Type> withType = t.map(ty -> (Specification<Type>) (rt,q, cb) -> cb.like(rt.get("type"), "%" + ty + "%")).orElse(null);
        return repo.findAll(Specification.anyOf(withId, withType)).stream().map(TypeResp::getTypeResp).toList();
    }

    public TypeResp save(Type type) {
        if(!find(Optional.empty(), Optional.of(type.getType())).isEmpty()){
            throw new EntityExistsException("This type is already exists.");
        }
        return TypeResp.getTypeResp(repo.save(type));
    }

    public boolean delete(Integer id){
        if(repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }else {
            return false;
        }
    }
    public TypeResp update( Type type) {
        if(repo.existsById(type.getId())){
            return TypeResp.getTypeResp(repo.save(type));
        }else {
            throw new EntityNotFoundException("There is no such type.");
        }
    }

    public Type findById(int id) {
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("There is no such type"));
    }
}
