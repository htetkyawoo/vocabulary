package com.example.vocabulary.repo;

import com.example.vocabulary.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepo extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

}
