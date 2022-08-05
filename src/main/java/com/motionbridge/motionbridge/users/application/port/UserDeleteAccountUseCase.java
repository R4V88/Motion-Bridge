package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.security.user.UserEntityDetails;

public interface UserDeleteAccountUseCase {
    void deleteUserById(Long id, UserEntityDetails user);
}
