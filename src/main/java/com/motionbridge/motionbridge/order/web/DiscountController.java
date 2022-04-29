package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase.CreateDiscountCommand;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase.SwitchStatusResponse;
import com.motionbridge.motionbridge.order.web.mapper.RestDiscount;
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

    final ManipulateDiscountUseCase discountService;

    @Operation(summary = "ADMIN, wyszukuje wszystkie dostepne znizki")
    @GetMapping
    public List<RestDiscount> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    @Operation(summary = "ADMIN, dodawanie nowego discounta")
    @PostMapping("/add")
    public void addNewDiscount(@RequestBody RestDiscountCommand command) {
        CreateDiscountCommand createDiscountCommand = command.toCreateCommand();
        discountService.addNewDiscount(createDiscountCommand);
    }

    @Operation(summary = "ADMIN, zmiana statusu nowego discounta po id z inActive / Active i na odwrót")
    @PutMapping("/{id}")
    public SwitchStatusResponse switchStatus(@PathVariable Long id) {
        return discountService.switchStatus(id);
    }

    @Operation(summary = "ADMIN, usunięcie discounta z bazy po id")
    @DeleteMapping("{id}")
    public void deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscountById(id);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestDiscountCommand {
        @NotBlank
        String code;
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
            return new CreateDiscountCommand(code, subscriptionType, subscriptionPeriod, startDate, duration, durationPeriod, value);
        }
    }
}
