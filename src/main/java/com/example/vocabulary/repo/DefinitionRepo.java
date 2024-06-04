package com.example.vocabulary.repo;

import com.example.vocabulary.entity.Definition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DefinitionRepo extends JpaRepository<Definition, LocalDateTime>, JpaSpecificationExecutor<Definition> {

    @Query("select count(*) > 0 from Definition d where d.vocabulary.id = ?1 and d.lang.id = ?2 and  d.type.id = ?3")
    boolean existsByVocabularyIdAndLangIdAndTypeId(long vocabularyId, long langId, long typeId);

    @Query("select d from Definition d where d.vocabulary.id = ?1 and d.lang.id = ?2 and  d.type.id = ?3")
    Optional<Definition> findByVocabularyIdAndLangIdAndTypeId(long vocabularyId, long langId, long typeId);
}
