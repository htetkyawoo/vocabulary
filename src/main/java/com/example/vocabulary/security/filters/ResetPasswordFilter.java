package com.example.vocabulary.security.filters;

import com.bastiaanjansen.otp.TOTPGenerator;
import com.example.vocabulary.entity.Account;
import com.example.vocabulary.service.AccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ResetPasswordFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var isReset = request.getRequestURI().contains("/reset");
        if(isReset){
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            UsernamePasswordAuthenticationToken
                                    .authenticated(null,
                                            null,
                                            List.of(new SimpleGrantedAuthority("ROLE_RESET"))));
        }

        filterChain.doFilter(request, response);

    }
}
