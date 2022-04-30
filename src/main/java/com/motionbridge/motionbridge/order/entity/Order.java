package com.motionbridge.motionbridge.order.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity {
    @Builder.Default
    Long discountId = 0L;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    UserEntity user;

    @Builder.Default
    BigDecimal currentPrice = new BigDecimal("00.00");

    @Builder.Default
    BigDecimal totalPrice = new BigDecimal("00.00");

    @Builder.Default
    Boolean isLocked = false;

    @Builder.Default
    Boolean activeDiscount = false;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;
}
