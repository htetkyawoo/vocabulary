package com.example.vocabulary.repo;

import com.example.vocabulary.entity.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LangRepo extends JpaRepository<Lang, Long>, JpaSpecificationExecutor<Lang> {
}
