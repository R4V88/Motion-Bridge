package com.motionbridge.motionbridge.security;


import org.springframework.security.core.userdetails.UserDetails;

public class UserSecurity {

    //TODO: Use as filter with @AuthenticationPrincipal
    public boolean isOwnerOrAdmin(String objectOwner, UserDetails user) {
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
