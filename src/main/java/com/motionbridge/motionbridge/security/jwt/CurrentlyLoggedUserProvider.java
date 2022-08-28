package com.motionbridge.motionbridge.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentlyLoggedUserProvider {

    private static final String ANONYMOUS_USERNAME = "Anonymous";

    public String getCurrentLoggedUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .flatMap(this::findPrincipal)
                .map(UserDetails::getUsername)
                .orElse(ANONYMOUS_USERNAME);
    }

    private Optional<UserDetails> findPrincipal(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            return Optional.ofNullable((UserDetails) authentication.getPrincipal());
        } else {
            return Optional.empty();
        }
    }
}
