package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface ManipulateUserDataUseCase {

    UpdatePasswordResponse updatePassword(UpdatePasswordCommand command, UserEntityDetails user);

    Optional<UserEntity> getUserById(Long id);

    UserEntity retrieveOrderByUserId(Long id, UserEntityDetails user);

    UserEntity getCurrentUserById(Long userId);

    Optional<UserEntity> findByUserEmailIgnoreCase(String email);

    SwitchResponse switchBlockStatus(Long id);

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

    @Value
    class SwitchResponse {
        public static SwitchResponse SUCCESS = new SwitchResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }
}