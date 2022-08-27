package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import com.motionbridge.motionbridge.users.web.mapper.RestPaginatedRichUser;
import com.motionbridge.motionbridge.users.web.mapper.RichRestUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface ManipulateUserDataUseCase {

    UpdatePasswordResponse updatePassword(UpdatePasswordCommand command, String user);

    UpdateNameResponse updateName(UpdateNameCommand command, String user);

    Optional<UserEntity> getUserById(Long id);

    Optional<UserEntity> getUserByEmail(String userEmail);

    UserEntity retrieveOrderByUserId(Long id, UserEntityDetails user);

    UserEntity getCurrentUserById(Long userId);

    Optional<UserEntity> findByUserEmailIgnoreCase(String email);

    SwitchResponse switchBlockStatus(Long id);

    List<RichRestUser> getAllUsers(String currentlyLoggedUser);

    @Value
    @Builder
    @AllArgsConstructor
    class UpdatePasswordCommand {
        String password;
    }

    @Value
    @Builder
    @AllArgsConstructor
    class UpdateNameCommand {
        String name;
    }

    @Value
    class UpdateNameResponse {
        public static UpdateNameResponse SUCCESS = new UpdateNameResponse(true, emptyList());

        boolean success;
        List<String> errors;
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