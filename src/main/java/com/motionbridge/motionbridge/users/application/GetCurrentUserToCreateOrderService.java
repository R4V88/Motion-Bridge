package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.users.application.port.GetUserToCreateOrderUseCase;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class GetCurrentUserToCreateOrderService implements GetUserToCreateOrderUseCase {

    final UserEntityRepository repository;

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
}
