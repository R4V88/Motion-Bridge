package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {
    final OrderRepository orderRepository;

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


