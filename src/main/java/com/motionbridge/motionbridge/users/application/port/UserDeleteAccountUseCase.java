package com.motionbridge.motionbridge.users.application.port;

public interface UserDeleteAccountUseCase {
    void deleteUserByUserEmail(String email);
}
