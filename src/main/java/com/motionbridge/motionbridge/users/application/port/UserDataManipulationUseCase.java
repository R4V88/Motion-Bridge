package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface UserDataManipulationUseCase {

    UpdatePasswordResponse updatePassword(UpdatePasswordCommand command);

    RegisterResponse register(String username, String password);

    Optional<UserEntity> findById(Long id);

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

    @Value
    @Builder
    @AllArgsConstructor
    class UpdatePasswordCommand {
        Long id;
        String password;
    }

    @Value
    class UpdatePasswordResponse {
        public static UpdatePasswordResponse SUCCESS = new UpdatePasswordResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }

}
