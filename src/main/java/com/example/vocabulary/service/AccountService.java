package com.example.vocabulary.service;

import com.example.vocabulary.entity.Account;
import com.example.vocabulary.entity.Photo;
import com.example.vocabulary.repo.AccountRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Account> accounts = search(Optional.of(username), Optional.of(username), false);
        Account account = accounts.stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Username or Password is wrong."));
        return new User(account.getEmail(), account.getPassword(), Stream.of(account.getRole().toString()).map(s -> new SimpleGrantedAuthority("ROLE_" + s)).toList());
    }

    public Account save(Account account) {
        List<Account> accounts = search(Optional.of(account.getEmail()), Optional.of(account.getName()), false);
        if (!accounts.isEmpty()) {
            accounts.stream().filter(a -> Objects.equals(a.getEmail(), account.getEmail())).findFirst()
                    .ifPresent(s -> {
                        throw new EntityExistsException("This email is used by other user.");
                    });
            accounts.stream().filter(a -> Objects.equals(a.getName(), account.getName())).findFirst()
                    .ifPresent(s -> {
                        throw new EntityExistsException("This name is used by other user.");
                    });

        }
        return repo.save(account);
    }

    public Account update(Account account) {
        var exists = findById(account.getEmail());
        BeanUtils.copyProperties(account, exists, "email", "password", "profile", "vocabularies", "role");
        return repo.save(exists);
    }

    public void updatePassword(String email, String password) {
        var account = findById(email);
        account.setPassword(password);
        repo.save(account);

    }

    public boolean delete(String account) {
        if (repo.existsById(account)) {
            repo.deleteById(account);
            return true;
        }
        return false;
    }

    private List<Account> search(Optional<String> email, Optional<String> username, boolean likeSearch) {
        Specification<Account> withEmail = email.map(e -> (Specification<Account>) (rt, q, cb) -> cb.equal(rt.get("email"), e)).orElse(null);
        Specification<Account> withUserName;
        if (likeSearch) {
            withUserName = username.map(e -> (Specification<Account>) (rt, q, cb) -> cb.like(rt.get("name"), "%" + e + "%")).orElse(null);
        } else {
            withUserName = username.map(e -> (Specification<Account>) (rt, q, cb) -> cb.equal(rt.get("name"), e)).orElse(null);
        }
        return repo.findAll(Specification.anyOf(withEmail, withUserName));
    }

    public List<Account> find(Optional<String> email, Optional<String> username) {
        return search(email, username, true);
    }

    public Account findById(String email) {
        var accountOpt = repo.findById(email);
        if (accountOpt.isEmpty()) {
            throw new EntityNotFoundException("There is no account register with %s".formatted(email));
        }
        return accountOpt.get();
    }

    public String updateProfile(String id, String url) {
        var account = findById(id);
        if(account.getProfile() == null){
            account.setProfile(new Photo(url));
            repo.save(account);
        }
        return url;
    }
}

