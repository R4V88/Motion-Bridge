package com.motionbridge.motionbridge.product.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
//    @Enumerated(EnumType.STRING)
    ProductName name;
    BigDecimal price;
    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
    Boolean isActive;
//    Integer animationQuantity;
//    Integer timePeriod;

    @Getter
    @AllArgsConstructor
    public
    enum ProductName {
        INSTAGRAM
    }
}
