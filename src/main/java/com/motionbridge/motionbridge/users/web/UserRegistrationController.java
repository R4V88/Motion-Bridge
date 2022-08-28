package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.users.application.port.UserRegisterationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;

@RestController
@AllArgsConstructor
@Tag(name = "/api/registration", description = "Users registration")
@RequestMapping("/api/register")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegistrationController {

    final UserRegisterationUseCase userRegisterationUseCase;

    @Operation(summary = "ALL, Rejestracja użytkownika")
    @ApiResponses(value = {
            @ApiResponse(description = "Account created, please check your mailbox to activate token", responseCode = "200"),
            @ApiResponse(description = "Invalid arguments", responseCode = "400")
    })
    @PostMapping()
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        return userRegisterationUseCase
                .register(command.name, command.email, command.password, command.acceptedTerms, command.acceptedNewsletter)
                .handle(
                        entity -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("""
                                {
                                "message" : "Account created, please check your mailbox to activate token"
                                }"""),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @Operation(summary = "Token confirmation")
    @ApiResponse(description = "OK", responseCode = "200")
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        final String mesage = userRegisterationUseCase.confirmToken(token);
        if(!mesage.equals("confirmed")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).location(URI.create("http://34.118.9.226:3000/sign-in?message=1")).build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://34.118.9.226:3000/sign-in?message=0")).build();
    }

    @Data
    static class RegisterCommand {
        @NotBlank(message = "Please provide valid name")
        String name;
        @Email
        @NotBlank(message = "Please provide valid email")
        String email;
        @Size(min = 8, max = 100)
        @NotEmpty(message = "Please provide valid password")
        String password;
        @NotNull(message = "Please accept terms to register account")
        Boolean acceptedTerms;
        @NotNull
        Boolean acceptedNewsletter;
    }
}
