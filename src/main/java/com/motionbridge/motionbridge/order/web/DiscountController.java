package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.ResponseDiscount;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase.CreateDiscountCommand;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase.SwitchStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "/api/discount", description = "Manipulate discount")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/discount")
public class DiscountController {

    final ManipulateDiscountUseCase manipulateDiscountUseCase;

    @Operation(summary = "ADMIN")
    @GetMapping
    public List<ResponseDiscount> getAllDiscounts() {
        return manipulateDiscountUseCase.getAllDiscounts();
    }

    @Operation(summary = "ADMIN")
    @PostMapping("/add")
    public void addNewDiscount(@RequestBody RestDiscountCommand command) {
        manipulateDiscountUseCase.addNewDiscount(command.toCreateCommand());
    }

    @Operation(summary = "ADMIN")
    @PutMapping("/{id}")
    public SwitchStatusResponse switchStatus(@PathVariable Long id) {
        return manipulateDiscountUseCase.switchStatus(id);
    }

    @Operation(summary = "ADMIN")
    @DeleteMapping("{id}")
    public void deleteDiscount(@PathVariable Long id) {
        manipulateDiscountUseCase.deleteDiscountById(id);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestDiscountCommand {
        @NotBlank
        String subscriptionType;
        @NotBlank
        String subscriptionPeriod;
        @NotNull
        LocalDateTime startDate;
        @NotNull
        Integer duration;
        @NotBlank
        String durationPeriod;
        @NotNull
        Integer value;

        CreateDiscountCommand toCreateCommand() {
            return new CreateDiscountCommand(subscriptionType, subscriptionPeriod, startDate, duration, durationPeriod, value);
        }
    }
}
