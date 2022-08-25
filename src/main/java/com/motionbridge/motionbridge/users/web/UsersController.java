package com.motionbridge.motionbridge.users.web;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;
import com.motionbridge.motionbridge.security.jwt.CurrentlyLoggedUserProvider;
import com.motionbridge.motionbridge.security.jwt.JwtConfig;
import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase.SwitchResponse;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase.UpdatePasswordCommand;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase.UpdatePasswordResponse;
import com.motionbridge.motionbridge.users.application.port.UserDeleteAccountUseCase;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import com.motionbridge.motionbridge.users.web.mapper.LoginCommand;
import com.motionbridge.motionbridge.users.web.mapper.RestSubscription;
import com.motionbridge.motionbridge.users.web.mapper.RestUser;
import io.jsonwebtoken.Jwts;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    final AuthenticationManager authenticationManager;
    final CurrentlyLoggedUserProvider currentlyLoggedUserProvider;
    final JwtConfig jwtConfig;
    final SecretKey secretKey;

    @Operation(summary = "Logowanie")
    @ApiResponses(value = {
            @ApiResponse(description = "Successfully logged in", responseCode = "200"),
            @ApiResponse(description = "Failed to find user", responseCode = "401")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginCommand loginCommand) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginCommand.getEmail(), loginCommand.getPassword())
            );

            UserEntityDetails userEntityDetails = (UserEntityDetails) authenticate.getPrincipal();

            final Optional<UserEntity> byUserEmailIgnoreCase = userService.findByUserEmailIgnoreCase(userEntityDetails.getUsername());

            Map<String, Object> claims = new HashMap<>();
            claims.put("acceptedNewsletter", byUserEmailIgnoreCase.get().getAcceptedNewsletter());
            claims.put("username", byUserEmailIgnoreCase.get().getLogin());
            claims.put("authorities", userEntityDetails.getAuthorities());

            String token = Jwts
                    .builder()
                    .setSubject(userEntityDetails.getUsername())
                    .setIssuer("motionbridge")
                    .addClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                    .signWith(secretKey)
                    .compact();

            return ResponseEntity.ok()
                    .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token)
                    .build();

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, zmiana hasła")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Invalid password", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/changePassword")
    public void changePassword(@Valid @RequestBody RestUserCommand command) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        UpdatePasswordResponse response = userService.updatePassword(command.toUpdatePasswordCommand(), currentLoggedUsername);
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, pobranie danych użytkownika - email, name, ActiveAccount, AcceptedNewsletter")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "User not found", responseCode = "404")
    })
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public RestUser getDetails() {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        return userService.getUserByEmail(currentLoggedUsername)
                .map(RestUser::toCreateRestUser)
                .orElseThrow(() -> new RuntimeException("User does not exist"));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, usunięcie konta")
    @ApiResponse(description = "When successfully deleted account", responseCode = "202")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping()
    public void deleteAccount() {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        deleteAccountUseCase.deleteUserByUserEmail(currentLoggedUsername);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, pobranie wszystkich orderów użytkowników")
    @GetMapping("/orders")
    public RestRichOrder getAllOrders() {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        return orderService.getAllOrdersWithSubscriptions(currentLoggedUsername);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, wszystkie subskrypcje po id usera")
    @GetMapping("/subscriptions")
    public ResponseEntity<List<RestSubscription>> getSubscriptions() {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        long id;
        if (userService.findByUserEmailIgnoreCase(currentLoggedUsername).isPresent()) {
            id = userService.findByUserEmailIgnoreCase(currentLoggedUsername).get().getId();
        } else {
            return ResponseEntity.notFound().build();
        }

        List<RestSubscription> subscriptions = subscriptionService
                .findAllByUserId(id)
                .stream()
                .filter(subscription -> subscription.getUser().getEmail().equals(currentLoggedUsername))
                .map(RestSubscription::toRestSubscription)
                .collect(Collectors.toList());
        if (subscriptions.size() != 0) {
            return ResponseEntity.ok(subscriptions);
        } else
            return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

        UpdatePasswordCommand toUpdatePasswordCommand() {
            return new UpdatePasswordCommand(password);
        }
    }
}
