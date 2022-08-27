package com.motionbridge.motionbridge.users.web.mapper;

import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class RichRestUser {
    Long id;
    String email;
    String name;
    Boolean isVerified;
    Boolean acceptedNewsletter;
    Boolean acceptedTerms;
    Boolean isBlocked;
    LocalDateTime createdAt;

    public static RichRestUser toCreateRichRestUser(UserEntity userEntity) {
        return new RichRestUser(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getLogin(),
                userEntity.getIsVerified(),
                userEntity.getAcceptedNewsletter(),
                userEntity.getAcceptedTerms(),
                userEntity.getIsBlocked(),
                userEntity.getCreatedAt()
        );
    }
}
