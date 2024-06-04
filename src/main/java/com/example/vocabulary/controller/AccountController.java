package com.example.vocabulary.controller;

import com.bastiaanjansen.otp.TOTPGenerator;
import com.example.vocabulary.entity.Account;
import com.example.vocabulary.http.req.ResetReq;
import com.example.vocabulary.http.resp.AccountResp;
import com.example.vocabulary.service.AccountService;
import com.example.vocabulary.service.MailService;
import com.example.vocabulary.service.fss.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@PropertySource({"classpath:storage.properties"})
@Tag(
        name = "Accounts"
)
@SecurityRequirement(
        name = "Bearer Auth"
)
public class AccountController {
    private final AccountService service;
    private final PasswordEncoder passwordEncoder;
    private final TOTPGenerator totpGenerator;
    private final MailService mailService;
    private final FileStorageService fileStorageService;

    @Value("${images.storage.location}")
    private String profileStorage;

    @Value("${profile.default.photo}")
    private String defaultProfile;

    @GetMapping
    public List<AccountResp> get(@RequestParam Optional<String> id, @RequestParam Optional<String> name){
        return service.find(id, name).stream().map(acc -> AccountResp.toAccountResp(acc, defaultProfile)).toList();
    };

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(@Validated({Account.AccountWithoutPassword.class, Account.AccountWithPassword.class}) @RequestBody Account account, BindingResult result) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        service.save(account);
        return "Successfully created";
    };

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountResp put(@Validated(Account.AccountWithoutPassword.class) @RequestBody Account account, BindingResult result){
        var acc = service.update(account);
        return AccountResp.toAccountResp(acc, defaultProfile);
    }

    @PatchMapping
    public String uploadProfile(@RequestPart("file") MultipartFile photo, @RequestPart("id") String id){
        return service.updateProfile(id, fileStorageService.save(photo, id, profileStorage + "/profiles"));
    }

    @PatchMapping("/reset")
    public String getResetCode(@RequestBody ResetReq resetReq){
        mailService.sendMail(service.findById(resetReq.email()));
        return "Reset Code is sent to %s".formatted(resetReq.email());
    }
    @PatchMapping("/reset/password")
    public String setResetPassword(@RequestBody ResetReq resetReq){
        if(totpGenerator.verify(resetReq.code())){
            var password = passwordEncoder.encode(resetReq.password());
            service.updatePassword(resetReq.email(), password);
            return "Password is changed. Please login again.";
        }
        throw new AuthenticationCredentialsNotFoundException("Wrong Code.");
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam String id){
        if(service.delete(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body("No Such account.");
    }
}
