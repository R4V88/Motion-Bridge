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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {

    @Builder.Default
    Boolean isActive = false;

    @Builder.Default
    transient LocalDateTime startDate = now();

    @Builder.Default
    transient LocalDateTime endDate = now();

    BigDecimal price;

    BigDecimal currentPrice;

    Integer animationsLimit;

    @Builder.Default
    Integer animationsLimitCounter = 0;

    String type;

    String timePeriod;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    UserEntity user;

    @JoinColumn(name = "order_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Order order;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;
}
