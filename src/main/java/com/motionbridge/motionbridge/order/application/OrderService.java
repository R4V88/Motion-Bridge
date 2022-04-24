package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.DiscountRepository;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase.CreateSubscriptionCommand;
import com.motionbridge.motionbridge.subscription.db.SubscriptionRepository;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService implements ManipulateOrderUseCase {

    final OrderRepository orderRepository;
    final DiscountRepository discountRepository;
    final ProductRepository productRepository;
    final SubscriptionRepository subscriptionRepository;
    final UserEntityRepository userEntityRepository;
    final SubscriptionUseCase subscriptionUseCase;

    @Override
    public void placeOrder(PlaceOrderCommand command) {
        Long userId = command.getUserId();
        Long productId = command.getProductId();
        Product product = productRepository.getById(productId);
        UserEntity user = userEntityRepository.getById(userId);

        Order order = Order.builder()
                .user(userEntityRepository.getById(userId))
                .subscription(
                        subscriptionUseCase
                                .addSubscription(
                                        CreateSubscriptionCommand
                                                .builder()
                                                .price(product.getPrice())
                                                .currentPrice(product.getPrice())
                                                .animationLimit(product.getAnimationQuantity())
                                                .type(String.valueOf(product.getName()))
                                                .user(user)
                                                .build())
                )
                .totalPrice(product.getPrice())
                .currentPrice(product.getPrice())
                .build();

        orderRepository.save(order);
    }



    @Override
    public void addProduktToOrder() {

    }

    @Override
    public void addDiscount() {

    }

    @Override
    public void deleteOrder(Long orderId) {

    }
}
