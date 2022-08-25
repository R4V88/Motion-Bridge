package com.motionbridge.motionbridge.users.web.mapper;

import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.Value;

@Value
public class RestUser {
    String email;
    String name;
    Boolean isVerified;
    Boolean acceptedNewsletter;

    public static RestUser toCreateRestUser(UserEntity userEntity) {
        return new RestUser(
                userEntity.getEmail(),
                userEntity.getLogin(),
                userEntity.getIsVerified(),
                userEntity.getAcceptedNewsletter()
        );
    }
}
