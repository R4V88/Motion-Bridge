package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase.UpdatePasswordCommand;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase.UpdatePasswordResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

    private final UserDataManipulationUseCase user;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        return user
                .register(command.username, command.password)
                .handle(
                        entity -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.badRequest().build()
                );
    }

    @Data
    static class RegisterCommand {
        @Email
        @NotBlank
        String username;
        @Size(min = 8, max = 100)
        @NotEmpty
        String password;
    }

    //Todo    @Secured()
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changePassword(@PathVariable Long id, @RequestBody RestUserCommand command) {
        UpdatePasswordResponse response = user.updatePassword(command.toUpdatePasswordCommand(id));
        if(!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Data
    private static class RestUserCommand {
        @NotBlank
        private String password;

        UpdatePasswordCommand toUpdatePasswordCommand(Long id) {
            return new UpdatePasswordCommand(id, password);
        }
    }

}
