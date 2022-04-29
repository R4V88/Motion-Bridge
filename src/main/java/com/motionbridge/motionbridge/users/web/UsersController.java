package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.users.web.mapper.RestSubscription;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase.UpdatePasswordCommand;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase.UpdatePasswordResponse;
import com.motionbridge.motionbridge.users.web.mapper.RestUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Tag(name = "/api/user", description = "Manipulate Users")
@RequestMapping("/api/user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersController {

    final UserDataManipulationUseCase user;
    final ManipulateOrderUseCase orderService;
    final SubscriptionUseCase subscriptionService;

    @Operation(summary = "ALL, Rejestracja użytkownika")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        return user
                .register(command.username, command.password)
                .handle(
                        entity -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.badRequest().build()
                );
    }

    //Todo    @Secured()
    @Operation(summary = "USER zalogowany, zmiana hasła")
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changePassword(@PathVariable Long id, @RequestBody RestUserCommand command) {
        UpdatePasswordResponse response = user.updatePassword(command.toUpdatePasswordCommand(id));
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Operation(summary = "USER zalogowany, pobranie danych użytkownika - LOGINU/E-MAILA")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestUser getById(@PathVariable Long id) {
        if (user.findById(id).isPresent()) {
            String login = user.findById(id).get().getUsername();
            return new RestUser(login);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "USER zalogowany, pobranie wszystkich orderów użytkowników")
    @GetMapping("/{id}/orders")
    @ResponseStatus(HttpStatus.OK)
    public RestRichOrder getAllOrders(@PathVariable Long id) {
        return orderService.findAllOrdersWithSubscriptions(id);
    }

    @Operation(summary = "USER zalogowany, wszystkie subskrypcje po id usera")
    @GetMapping("/{id}/subscription")
    @ResponseStatus(HttpStatus.OK)
    public List<RestSubscription> getSubscriptions(@PathVariable Long id) {
        List<RestSubscription> subscriptions = subscriptionService
                .findAllByUserId(id)
                .stream()
                .map(RestSubscription::toRestSubscription)
                .collect(Collectors.toList());
        if (subscriptions.size() != 0) {
            return subscriptions;
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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

    @Data
    private static class RestUserCommand {
        @NotBlank
        private String password;

        UpdatePasswordCommand toUpdatePasswordCommand(Long id) {
            return new UpdatePasswordCommand(id, password);
        }
    }
}
