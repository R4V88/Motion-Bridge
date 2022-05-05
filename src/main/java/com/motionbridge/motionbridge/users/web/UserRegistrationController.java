package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.users.application.port.UserRegisterationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Tag(name = "/api/registration", description = "Users registration")
@RequestMapping("/api/registration")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegistrationController {

    final UserRegisterationUseCase userRegisterationUseCase;

    @Operation(summary = "ALL, Rejestracja użytkownika")
    @PostMapping()
    public ResponseEntity<?> register(@Valid @RequestBody UsersController.RegisterCommand command) {
        return userRegisterationUseCase
                .register(command.name, command.email, command.password, command.acceptedTerms, command.acceptedNewsletter)
                .handle(
                        entity -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.badRequest().build()
                );
    }

    @Operation(summary = "ALL, Token confirmation ***Tego endpointu nigdzie nie podpinacie, link aktywacyjny leciu w majlu razem z tym endpointem***")
    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) {
        return userRegisterationUseCase.confirmToken(token);
    }
}