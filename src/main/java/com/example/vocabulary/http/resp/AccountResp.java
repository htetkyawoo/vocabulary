package com.example.vocabulary.http.resp;

import com.example.vocabulary.entity.Account;
import com.example.vocabulary.entity.Photo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;

@Getter
public class AccountResp {

    private final String email;
    private final String name;
    private final Account.Role role;
    private final ArrayList<VocabularyResp> vocabularies = new ArrayList<>();
    private final Photo profile;

    private AccountResp(Account account){
        this.email = account.getEmail();
        this.name = account.getName();
        this.role = account.getRole();
        account.getVocabularies().forEach(v -> vocabularies.add(VocabularyResp.toVocabularyResp(v, null)));
        this.profile = account.getProfile();
    }

    public static AccountResp toAccountResp(Account account, String defaultProfile){
        if(account.getProfile() == null){
            account.setProfile(new Photo(defaultProfile));
        }
        return new AccountResp(account);
    }
}
