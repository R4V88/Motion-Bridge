package com.motionbridge.motionbridge.security.user;

import com.motionbridge.motionbridge.security.config.AdminConfig;
import com.motionbridge.motionbridge.users.application.port.SecurityGetUserUseCase;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MotionbridgeUserDetailsService implements UserDetailsService {

    private final SecurityGetUserUseCase repository;
    private final AdminConfig config;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (config.getUsername().equalsIgnoreCase(email)) {
            return config.adminUser();
        }
        return repository
                .findByUserEmailIgnoreCase(email)
                .map(x -> new UserEntity(x))
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public String signUpUser(UserEntity userEntity) {
        return "";
    }
}
