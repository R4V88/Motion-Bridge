package com.motionbridge.motionbridge.subscription.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "uuid")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Subscription extends BaseEntity {
    Boolean isActive;
    LocalDateTime startDate;
    LocalDateTime endDate;
    BigDecimal price;
    BigDecimal currentPrice;
    Integer animationsLimit;
    Integer animationsLimitCounter;
    String type;
    Long userId;
}
