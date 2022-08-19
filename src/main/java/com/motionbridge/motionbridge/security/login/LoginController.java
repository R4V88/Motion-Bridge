package com.motionbridge.motionbridge.security.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/login")
public class LoginController {

    @PostMapping
    public void login(@RequestBody LoginCommand command) {
    }
}
