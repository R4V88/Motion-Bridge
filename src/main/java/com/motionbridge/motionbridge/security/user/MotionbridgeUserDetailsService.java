package com.motionbridge.motionbridge.security.user;

import com.motionbridge.motionbridge.security.config.AdminConfig;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MotionbridgeUserDetailsService implements UserDetailsService {

    private final ManipulateUserDataUseCase userDataManipulationUseCase;
    private final AdminConfig config;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (config.getUsername().equalsIgnoreCase(email)) {
            return config.adminUser();
        }
        return userDataManipulationUseCase
                .findByUserEmailIgnoreCase(email)
                .map(x -> new UserEntityDetails(x))
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
