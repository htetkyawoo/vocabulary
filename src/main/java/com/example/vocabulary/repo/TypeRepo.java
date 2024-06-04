package com.example.vocabulary.repo;

import com.example.vocabulary.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepo extends JpaRepository<Type, Integer>, JpaSpecificationExecutor<Type> {
    boolean existsAllByIdIsIn(List<Integer> types);
}
