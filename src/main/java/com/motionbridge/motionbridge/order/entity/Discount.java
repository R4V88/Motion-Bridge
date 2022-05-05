package com.motionbridge.motionbridge.order.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
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

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Discount extends BaseEntity {
    //kod
    String code;

    //Znizka na typ Subskrypcji pod względem name
    @Enumerated(EnumType.STRING)
    SubscriptionType subscriptionType;

    //Znizka na typ Subskrypcji pod względem czasu trwania subskrypcji
    @Enumerated(EnumType.STRING)
    SubscriptionPeriod subscriptionPeriod;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;

    LocalDateTime startDate;

    //Jednostki czasowe w dniach lub godzinach
    @Enumerated(EnumType.STRING)
    DurationPeriod durationPeriod;

    //Ile jednostek czasowych
    Integer duration;

    LocalDateTime endDate;

    Boolean isActive = false;

    //procentowa zniżka wyrazona liczba calkowita np 20% to 20.
    Integer value;

    public Discount(String code,
                    SubscriptionType subscriptionType,
                    SubscriptionPeriod subscriptionPeriod,
                    LocalDateTime startDate,
                    DurationPeriod durationPeriod,
                    Integer duration,
                    LocalDateTime endDate,
                    Integer value) {
        this.code = code;
        this.subscriptionType = subscriptionType;
        this.subscriptionPeriod = subscriptionPeriod;
        this.startDate = startDate;
        this.durationPeriod = durationPeriod;
        this.duration = duration;
        this.endDate = endDate;
        this.value = value;
    }
}
