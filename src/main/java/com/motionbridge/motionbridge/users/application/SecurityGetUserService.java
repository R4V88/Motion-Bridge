package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.users.application.port.SecurityGetUserUseCase;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SecurityGetUserService implements SecurityGetUserUseCase {

    final UserEntityRepository repository;

    @Override
    public Optional<UserEntity> findByUserEmailIgnoreCase(String email) {
        return repository.findByEmailIgnoreCase(email);
    }
}
