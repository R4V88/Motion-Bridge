package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@AllArgsConstructor
@Service
public class UserService implements UserDataManipulationUseCase {

    private final UserEntityRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public UpdatePasswordResponse updatePassword(UpdatePasswordCommand command) {
        return repository.findUserById(command.getId())
                .map(user -> {
                    updateActualPassword(command, user);
                    log.warn("Password has been changed");
                    return UpdatePasswordResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdatePasswordResponse(false, Collections.singletonList("New password is same as old")));
    }

    private UserEntity updateActualPassword(UpdatePasswordCommand command, UserEntity user) {
        if(!command.getPassword().equals(user.getPassword())) {
            user.setPassword(encoder.encode(command.getPassword()));
        }
        return null;
    }

    @Transactional
    @Override
    public RegisterResponse register(String username, String password) {
        if (repository.findByUsernameIgnoreCase(username).isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity entity = new UserEntity(username, encoder.encode(password));
        return RegisterResponse.success(repository.save(entity));
    }
}
