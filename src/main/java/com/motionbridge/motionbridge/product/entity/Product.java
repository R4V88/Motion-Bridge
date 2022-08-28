package com.motionbridge.motionbridge.product.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
import com.motionbridge.motionbridge.subscription.entity.Currency;
import com.motionbridge.motionbridge.subscription.entity.ProductName;
import com.motionbridge.motionbridge.subscription.entity.TimePeriod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    @Enumerated(EnumType.STRING)
    ProductName type;

    String title;

    BigDecimal price;

    @Enumerated(EnumType.STRING)
    Currency currency;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;

    Boolean isActive = false;

    Integer animationQuantity;

    @Enumerated(EnumType.STRING)
    TimePeriod timePeriod;

    String background;

    public Product(ProductName type, String title, BigDecimal price, Currency currency, Integer animationQuantity, TimePeriod timePeriod, String background) {
        this.type = type;
        this.title = title;
        this.price = price;
        this.currency = currency;
        this.animationQuantity = animationQuantity;
        this.timePeriod = timePeriod;
        this.background = background;
    }
}
