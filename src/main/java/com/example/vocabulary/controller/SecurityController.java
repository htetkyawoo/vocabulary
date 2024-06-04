package com.example.vocabulary.controller;

import com.example.vocabulary.entity.SecurityToken;
import com.example.vocabulary.entity.Account;
import com.example.vocabulary.http.req.LogInReq;
import com.example.vocabulary.security.JwtProvider;
import com.example.vocabulary.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
        name = "LogIn"
)
@SecurityRequirement(
        name = ""
)
public class SecurityController {

    private final JwtProvider jwtProvider;
    private final AccountService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public SecurityToken login(@RequestBody LogInReq user) {
        UserDetails userDetails = service.loadUserByUsername(user.getUsername());
        if (passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            var aut = UsernamePasswordAuthenticationToken.authenticated(user.getUsername(), null, userDetails.getAuthorities());
            var token = jwtProvider.generate(aut);
            return new SecurityToken(token);
        }else {
            throw new BadCredentialsException("Username or Password is wrong.");
        }
    }
}
