package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.users.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.users.application.port.UserDeleteAccountUseCase;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDeleteAccountService implements UserDeleteAccountUseCase {
    final UserEntityRepository repository;
    final ManipulateSubscriptionUseCase subscriptionUseCase;
    final ManipulateOrderUseCase orderUseCase;
    final ConfirmationTokenUseCase confirmationTokenUseCase;

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        subscriptionUseCase.deleteAllByUserId(id);
        orderUseCase.deleteAllOrdersByUserId(id);
        confirmationTokenUseCase.deleteTokenByUserId(id);
        repository.deleteById(id);
    }
}
