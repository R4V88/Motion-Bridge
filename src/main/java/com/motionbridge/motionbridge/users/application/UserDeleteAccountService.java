package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.security.user.UserSecurity;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.users.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.users.application.port.UserDeleteAccountUseCase;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDeleteAccountService implements UserDeleteAccountUseCase {
    final UserEntityRepository repository;
    final ManipulateSubscriptionUseCase subscriptionUseCase;
    final ManipulateOrderUseCase orderUseCase;
    final ConfirmationTokenUseCase confirmationTokenUseCase;
    final UserSecurity userSecurity;

    @Transactional
    @Override
    public void deleteUserById(Long id, UserEntityDetails user) {
        if (userSecurity.isOwnerOrAdmin(repository.findUserById(id).get().getEmail(), user)) {
            log.warn("Executing removing user with id: " + id);
            subscriptionUseCase.deleteAllByUserId(id);
            orderUseCase.deleteAllOrdersByUserId(id);
            confirmationTokenUseCase.deleteTokenByUserId(id);
            repository.deleteById(id);
            if (repository.findUserById(id).isEmpty()) {
                log.info("User with id: " + id + " successfully removed");
            }
        }
    }
}
