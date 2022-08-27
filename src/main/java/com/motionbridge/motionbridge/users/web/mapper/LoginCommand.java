package com.motionbridge.motionbridge.users.web.mapper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginCommand {
    @NotNull @Email String email;
    @NotNull String password;
}
