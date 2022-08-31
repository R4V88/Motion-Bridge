package com.motionbridge.motionbridge.product.web;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.CreateProductCommand;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.CreateParameter;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.CreatePresentation;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.SwitchStatusResponse;
import com.motionbridge.motionbridge.product.web.mapper.RestActiveProduct;
import com.motionbridge.motionbridge.product.web.mapper.RestProduct;
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
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Tag(name = "/api/products", description = "Manipulate Products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    final ManipulateProductUseCase productService;

    @Operation(summary = "ALL, wszystkie AKTYWNE produkty")
    @GetMapping("/active")
    public List<RestActiveProduct> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN, dodanie nowego produktu")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Invalid arguments", responseCode = "400")
    })
    @PostMapping()
    public ResponseEntity<Object> addNewProduct(@Valid @RequestBody RestProductCommand command) {
        return productService.addProduct(command.toCreateProductCommand())
                .handle(
                        newProduct -> ResponseEntity.ok().body(newProduct),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN, pobranie wszystkich produktow AKTYWNYCH i NIEAKTYWNYCH")
    @GetMapping()
    public List<RestProduct> getAllProducts() {
        return productService.getAllProducts();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN, zmiana statusu produktu z inActive na Active i na odwrót")
    @ApiResponses(value = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Status change failed", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{productId}")
    public void switchStatus(@PathVariable Long productId) {
        SwitchStatusResponse response = productService.switchStatus(productId);
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN, usunięcie produktu")
    @ApiResponses(value = {
            @ApiResponse(description = "NO_CONTENT", responseCode = "202"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
    }

    @Data
    public static class RestProductCommand {
        @NotBlank(message = "Please provide valid product type")
        String type;
        @NotBlank(message = "Please provide valid product name")
        String title;
        @NotBlank(message = "Please provide valid currency")
        String currency;
        @NotNull(message = "Please provide valid product time period")
        String timePeriod;
        @NotNull(message = "Please provide valid product animation quantity")
        Integer animationQuantity;
        @NotNull(message = "Please provide valid product price with format like 0.00")
        @DecimalMin("0.00")
        BigDecimal price;
        String background;
        List<CreatePresentation> presentations;
        List<CreateParameter> parameters;

        CreateProductCommand toCreateProductCommand() {
            return new CreateProductCommand(type, title, currency, timePeriod, animationQuantity, price, background, presentations, parameters);
        }
    }
}
