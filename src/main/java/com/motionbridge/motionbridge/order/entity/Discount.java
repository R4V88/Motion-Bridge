package com.motionbridge.motionbridge.order.entity;

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
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Discount extends BaseEntity {
    @Enumerated
    Type type;
    @Enumerated
    Period period;
    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
    LocalDateTime endDate;
    Boolean isActive;
    Integer value;

    public enum Period {
        MONTH,
        YEAR
    }

    public enum Type {
        ALL,
        INSTAGRAM
    }
}
