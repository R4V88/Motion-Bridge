package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;
import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.security.user.UserSecurity;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase.SwitchResponse;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase.UpdatePasswordCommand;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase.UpdatePasswordResponse;
import com.motionbridge.motionbridge.users.application.port.UserDeleteAccountUseCase;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import com.motionbridge.motionbridge.users.web.mapper.RestSubscription;
import com.motionbridge.motionbridge.users.web.mapper.RestUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

import static com.motionbridge.motionbridge.users.web.mapper.RestUser.toCreateRestUser;

@RestController
@AllArgsConstructor
@Tag(name = "/api/users", description = "Manipulate Users")
@RequestMapping("/api/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersController {

    final ManipulateUserDataUseCase userService;
    final ManipulateOrderUseCase orderService;
    final ManipulateSubscriptionUseCase subscriptionService;
    final UserDeleteAccountUseCase deleteAccountUseCase;
    final UserSecurity userSecurity;

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, zmiana hasła")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Invalid password", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/changePassword")
    public void changePassword(@PathVariable Long id, @Valid @RequestBody RestUserCommand command, @AuthenticationPrincipal UserEntityDetails user) {
        UpdatePasswordResponse response = userService.updatePassword(command.toUpdatePasswordCommand(id), user);
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, pobranie danych użytkownika - email, name, ActiveAccount, AcceptedNewsletter")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "User not found", responseCode = "404")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestUser getById(@PathVariable Long id, @AuthenticationPrincipal UserEntityDetails user) {
        UserEntity userEntity = userService.retrieveOrderByUserId(id, user);
        if (userEntity.getId().equals(id)) {
            return toCreateRestUser(userEntity);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, usunięcie konta")
    @ApiResponse(description = "When successfully deleted account", responseCode = "202")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, @AuthenticationPrincipal UserEntityDetails user) {
        deleteAccountUseCase.deleteUserById(id, user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, pobranie wszystkich orderów użytkowników")
    @GetMapping("/{id}/orders")
    public RestRichOrder getAllOrders(@PathVariable Long id, @AuthenticationPrincipal UserEntityDetails user) {
        return orderService.getAllOrdersWithSubscriptions(id, user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, wszystkie subskrypcje po id usera")
    @GetMapping("/{id}/subscription")
    public ResponseEntity<List<RestSubscription>> getSubscriptions(@PathVariable Long id, @AuthenticationPrincipal UserEntityDetails user) {
        List<RestSubscription> subscriptions = subscriptionService
                .findAllByUserId(id)
                .stream()
                .filter(subscription -> userSecurity.isOwnerOrAdmin(subscription.getUser().getEmail(), user))
                .map(RestSubscription::toRestSubscription)
                .collect(Collectors.toList());
        if (subscriptions.size() != 0) {
            return ResponseEntity.ok(subscriptions);
        } else
            return ResponseEntity.notFound().build();
    }

    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "ADMIN, zmiana statusu uzytkownika po id z unBlocked / Blocked i na odwrót")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "When successfully blocked account"),
            @ApiResponse(responseCode = "400", description = "When blocking was a failure")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/block")
    public void blockUserById(@PathVariable Long id) {
        SwitchResponse switchResponse = userService.switchBlockStatus(id);
        if (!switchResponse.isSuccess()) {
            String message = String.join(", ", switchResponse.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Data
    private static class RestUserCommand {
        @NotBlank(message = "Please provide valid new password")
        private String password;

        UpdatePasswordCommand toUpdatePasswordCommand(Long id) {
            return new UpdatePasswordCommand(id, password);
        }
    }
}
