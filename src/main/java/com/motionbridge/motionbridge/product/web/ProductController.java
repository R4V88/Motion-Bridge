package com.motionbridge.motionbridge.product.web;

import com.motionbridge.motionbridge.product.application.RestActiveProduct;
import com.motionbridge.motionbridge.product.application.RestProduct;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.AddProductCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

import java.util.List;
@Tag(name = "/api/product", description = "Manipulate Products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {

    final ManipulateProductUseCase manipulateProduct;

    @Operation(summary = "ALL")
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<RestActiveProduct> getActiveProducts() {
        return manipulateProduct.getActiveProducts();
    }

    @Operation(summary = "ADMIN")
    @PostMapping("/add")
    public ResponseEntity<Object> addNewProduct(@RequestBody AddProductCommand command) {
        return manipulateProduct.addProduct(command)
                .handle(
                        newProduct -> ResponseEntity.ok().build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @Operation(summary = "ADMIN")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<RestProduct> getAllProducts() {
        return manipulateProduct.getAllProducts();
    }

    @Operation(summary = "ADMIN")
    @PutMapping("{id}")
    public void switchStatus(@PathVariable Long id) {
        manipulateProduct.switchStatus(id);
    }

}
