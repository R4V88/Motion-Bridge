package com.motionbridge.motionbridge.subscription.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
import com.motionbridge.motionbridge.order.entity.Order;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Getter
@Setter
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Subscription extends BaseEntity {

    Boolean isActive = false;

    transient LocalDateTime startDate = now();

    transient LocalDateTime endDate = now();

    BigDecimal price = new BigDecimal("00.00");

    BigDecimal currentPrice = new BigDecimal("00.00");

    Integer animationsLimit;

    Integer animationsLimitCounter = 0;

    String type;

    String timePeriod;

    Long productId;

    Boolean autoRenew = true;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    UserEntity user;

    @JoinColumn(name = "order_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Order order;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;

    public Subscription(BigDecimal price,
                        BigDecimal currentPrice,
                        Integer animationsLimit,
                        String type,
                        String timePeriod,
                        Long productId,
                        UserEntity user,
                        Order order) {
        this.price = price;
        this.currentPrice = currentPrice;
        this.animationsLimit = animationsLimit;
        this.type = type;
        this.timePeriod = timePeriod;
        this.productId = productId;
        this.user = user;
        this.order = order;
    }
}

