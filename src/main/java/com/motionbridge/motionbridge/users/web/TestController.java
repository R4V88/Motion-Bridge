package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.security.jwt.CurrentlyLoggedUserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {
    final CurrentlyLoggedUserProvider currentlyLoggedUserProvider;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping()
    public String helloPrinciple(@RequestBody String hello) {

//        UserEntityDetails userEntityDetails1 = userEntityDetails;
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();

        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.warn(currentLoggedUsername);
        return currentLoggedUsername;
    }
}
