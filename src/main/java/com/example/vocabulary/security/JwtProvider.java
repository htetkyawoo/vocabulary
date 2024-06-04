package com.example.vocabulary.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtProvider {

    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private static final Calendar expiration = Calendar.getInstance();

    public Authentication authenticate(String token) {
        var jwt = Jwts.parser().verifyWith(key)
                .build().parseSignedClaims(token.substring("Bearer ".length()));

        var username = jwt.getPayload().getSubject();
        List<SimpleGrantedAuthority> authorities = Arrays.stream(jwt.getPayload().get("roles", String.class).split(",")).map(SimpleGrantedAuthority::new).toList();

        return UsernamePasswordAuthenticationToken.authenticated(username, null, authorities);
    }

    public String generate(Authentication authentication) {
        if (!Objects.isNull(authentication) && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            expiration.add(Calendar.MINUTE, 10);
            var token = Jwts.builder().issuer("htet-kyaw-oo").issuedAt(new Date()).expiration(expiration.getTime())
                    .signWith(key)
                    .subject(authentication.getName())
                    .claim("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .compact();
            return "Bearer %s".formatted(token);
        }
        throw new AccessDeniedException("Authentication Error");
    }
}
