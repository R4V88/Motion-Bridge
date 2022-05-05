package com.motionbridge.motionbridge.security.login;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginCommand {
    String email;
    String password;
}