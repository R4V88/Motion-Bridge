package com.motionbridge.motionbridge.security.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@ConfigurationProperties("app.security.admin")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminConfig {
    String username;
    String password;
    Set<String> role;

    public User adminUser() {
        return new User(
                username,
                password,
                role.stream().map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toSet())
        );
    }
}
