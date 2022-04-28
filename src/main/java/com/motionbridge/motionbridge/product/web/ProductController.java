package com.motionbridge.motionbridge.product.web;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.CreateProductCommand;
import com.motionbridge.motionbridge.product.web.mapper.RestActiveProduct;
import com.motionbridge.motionbridge.product.web.mapper.RestProduct;
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

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "/api/product", description = "Manipulate Products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {

    final ManipulateProductUseCase manipulateProduct;

    @Operation(summary = "ALL, wszystkie AKTYWNE produkty")
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<RestActiveProduct> getActiveProducts() {
        return manipulateProduct.getActiveProducts();
    }

    @Operation(summary = "ADMIN, dodanie nowego produktu")
    @PostMapping("/add")
    public ResponseEntity<Object> addNewProduct(@RequestBody RestProductCommand command) {
        return manipulateProduct.addProduct(command.toCreateProductCommand())
                .handle(
                        newProduct -> ResponseEntity.ok().build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @Operation(summary = "ADMIN, pobranie wszystkich produktow AKTYWNYCH i NIEAKTYWNYCH")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<RestProduct> getAllProducts() {
        return manipulateProduct.getAllProducts();
    }

    @Operation(summary = "ADMIN, zmiana statusu produktu z inActive na Active i na odwrót")
    @PutMapping("{id}")
    public void switchStatus(@PathVariable Long id) {
        manipulateProduct.switchStatus(id);
    }

    @Data
    public static class RestProductCommand {
        Integer animationQuantity;
        String name;
        String currency;
        String timePeriod;
        BigDecimal price;

        CreateProductCommand toCreateProductCommand() {
            return new CreateProductCommand(animationQuantity, name, currency, timePeriod, price);
        }
    }

}
