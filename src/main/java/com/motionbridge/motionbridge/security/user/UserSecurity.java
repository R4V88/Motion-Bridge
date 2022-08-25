package com.motionbridge.motionbridge.security.user;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public boolean isOwnerOrAdmin(String objectOwner, UserEntityDetails user) {
        return isAdmin(user) || isOwner(objectOwner, user);
    }

    private boolean isOwner(String objectOwner, UserDetails user) {
        return user.getUsername().equalsIgnoreCase(objectOwner);
    }

    private boolean isAdmin(UserDetails user) {
        return user
                .getAuthorities()
                .stream()
                .anyMatch(
                        a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN")
                );
    }
}
