package com.example.vocabulary.repo;

import com.example.vocabulary.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VocabularyRepo extends JpaRepository<Vocabulary, Long>, JpaSpecificationExecutor<Vocabulary> {
    Optional<Vocabulary> findBySpelling(String spelling);

}
