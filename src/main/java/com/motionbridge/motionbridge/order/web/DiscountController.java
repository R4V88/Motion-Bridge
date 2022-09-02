package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase.CreateDiscountCommand;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase.SwitchStatusResponse;
import com.motionbridge.motionbridge.order.web.mapper.RestDiscount;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "/api/discounts", description = "Manipulate discount")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/discounts")
public class DiscountController {

    final ManipulateDiscountUseCase discountService;

    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "Logged ADMIN, returns all discounts")
    @GetMapping
    public List<RestDiscount> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "Logged ADMIN, adds new discount")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "201"),
            @ApiResponse(description = "Invalid arguments", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponseEntity<?> addNewDiscount(@Valid @RequestBody RestDiscountCommand command) {
        CreateDiscountCommand createDiscountCommand = command.toCreateCommand();
        return discountService.addNewDiscount(createDiscountCommand)
                .handle(
                        discount -> ResponseEntity.accepted().body(discount),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "Logged ADMIN, changes the discount status to active or inactive")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Status change failed", responseCode = "400")
    })
    @PutMapping("/{discountId}")
    public SwitchStatusResponse switchStatus(@PathVariable Long discountId) {
        SwitchStatusResponse switchStatusResponse = discountService.switchStatus(discountId);
        if (!switchStatusResponse.isSuccess()) {
            String message = String.join(", ", switchStatusResponse.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return switchStatusResponse;
    }

    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "Logged ADMIN, removes the discount by id")
    @ApiResponse(description = "When successfully deleted discount", responseCode = "202")
    @DeleteMapping("/{discountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiscount(@PathVariable Long discountId) {
        discountService.deleteDiscountById(discountId);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestDiscountCommand {
        @NotBlank(message = "Please provide a code")
        String code;
        @NotBlank(message = "Please provide valid subscription type")
        String subscriptionType;
        @NotBlank(message = "Please provide valid subscription period")
        String subscriptionPeriod;
        @NotNull(message = "Please provide valid start date")
        LocalDateTime startDate;
        @NotNull(message = "Please provide valid discount duration")
        Integer duration;
        @NotBlank(message = "Please provide valid discount duration period")
        String durationPeriod;
        @NotNull(message = "Please provide valid discount value")
        Integer value;

        CreateDiscountCommand toCreateCommand() {
            return new CreateDiscountCommand(code, subscriptionType, subscriptionPeriod, startDate, duration, durationPeriod, value);
        }
    }
}
