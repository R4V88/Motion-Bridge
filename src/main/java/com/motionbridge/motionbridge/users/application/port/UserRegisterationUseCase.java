package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.users.entity.UserEntity;

public interface UserRegisterationUseCase {
    RegisterResponse register(String login, String email, String password, Boolean acceptedTerms, Boolean acceptedNewsletter);

    void confirmToken(String token);

    class RegisterResponse extends Either<String, UserEntity> {

        public RegisterResponse(boolean success, String left, UserEntity right) {
            super(success, left, right);
        }

        public static RegisterResponse success(UserEntity right) {
            return new RegisterResponse(true, null, right);
        }

        public static RegisterResponse failure(String left) {
            return new RegisterResponse(false, left, null);
        }

    }
}
