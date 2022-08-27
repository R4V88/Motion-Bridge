package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.security.user.UserSecurity;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManipulateUserDataService implements ManipulateUserDataUseCase {

    final UserEntityRepository repository;
    final PasswordEncoder encoder;
    final UserSecurity userSecurity;


    @Transactional
    @Override
    public UpdatePasswordResponse updatePassword(UpdatePasswordCommand command, String userEmail) {
        return repository.findByEmailIgnoreCase(userEmail)
                .filter(userByEmail -> userByEmail.getEmail().equals(userEmail))
                .map(userByEmail -> {
                    updateActualPassword(command, userByEmail);
                    log.warn("Password for user with id: {} has been changed", userByEmail.getId());
                    return UpdatePasswordResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdatePasswordResponse(false, Collections.singletonList("New password for user with name " + userEmail + " is same as old")));
    }

    private void updateActualPassword(UpdatePasswordCommand command, UserEntity user) {
        if (!command.getPassword().equals(user.getPassword())) {
            user.setPassword(encoder.encode(command.getPassword()));
        }
    }

    @Transactional
    @Override
    public UpdateNameResponse updateName(UpdateNameCommand command, String userEmail) {
        return repository.findByEmailIgnoreCase(userEmail)
                .filter(userByEmail -> userByEmail.getEmail().equals(userEmail))
                .map(userByEmail -> {
                    updateActualName(command, userByEmail);
                    log.warn("Name for user with id: {} has been changed", userByEmail.getId());
                    return UpdateNameResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateNameResponse(false, Collections.singletonList("New password for user with name " + userEmail + " is same as old")));
    }

    private void updateActualName(UpdateNameCommand command, UserEntity user) {
        if (!command.getName().equals(user.getLogin())) {
            user.setLogin(command.getName());
//            repository.save(user);
        }
    }

    @Override
    public UserEntity retrieveOrderByUserId(Long id, UserEntityDetails user) {
        Optional<UserEntity> userById = getUserById(id);
        UserEntity userEntity;
        if (userById.isPresent() && userSecurity.isOwnerOrAdmin(userById.get().getEmail(), user)) {
            userEntity = userById.get();
            return userEntity;
        } else {
            throw new NoSuchElementException("User with id: " + id + "does not exist");
        }
    }

    public Optional<UserEntity> getUserByEmail(String userEmail) {
        Optional<UserEntity> userByEmail = repository.findByEmailIgnoreCase(userEmail);
        UserEntity userEntity;
        if (userByEmail.isPresent()) {
            userEntity = userByEmail.get();
            return Optional.of(userEntity);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        Optional<UserEntity> user = repository.findById(id);
        UserEntity userEntity;
        if (user.isPresent()) {
            userEntity = user.get();
            return Optional.of(userEntity);
        } else {
            throw new NoSuchElementException("User with id: " + id + "does not exist");
        }
    }

    @Override
    public UserEntity getCurrentUserById(Long userId) {
        Optional<UserEntity> userTemp = getUserById(userId);
        UserEntity userToGet;
        if (userTemp.isPresent()) {
            userToGet = userTemp.get();
        } else {
            userToGet = new UserEntity();
            log.info("User with id: " + userId + " not found");
        }
        return userToGet;
    }

    @Override
    public Optional<UserEntity> findByUserEmailIgnoreCase(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

    @Transactional
    @Override
    public SwitchResponse switchBlockStatus(Long id) {
        return repository.findById(id)
                .map(product -> {
                    switchBlockedStatus(id);
                    return SwitchResponse.SUCCESS;
                })
                .orElseGet(() -> new SwitchResponse(false, Collections.singletonList("Could not change status")));
    }

    private void switchBlockedStatus(Long id) {
        repository.getById(id).setIsBlocked(repository.getById(id).getIsBlocked() != null && !repository.getById(id).getIsBlocked());
    }
}