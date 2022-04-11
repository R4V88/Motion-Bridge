package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.users.application.port.UserRegisterationUseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UsersController {

    private final UserRegisterationUseCase register;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        return register
                .register(command.username, command.password)
                .handle(
                        entity -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.badRequest().build()
                );
    }

    @Data
    static class RegisterCommand {
        @Email
        String username;
        @Size(min = 8, max = 100)
        String password;
    }
}
