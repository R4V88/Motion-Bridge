package com.motionbridge.motionbridge.product.web;

import com.motionbridge.motionbridge.product.application.RestActiveProduct;
import com.motionbridge.motionbridge.product.application.RestProduct;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.AddProductCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {

    final ManipulateProductUseCase manipulateProduct;

    //Todo Security ALL
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<RestActiveProduct> getActiveProducts() {
        return manipulateProduct.getActiveProducts();
    }

    //Todo Security Admin
    @PostMapping("/add")
    public ResponseEntity<Object> addNewProduct(@RequestBody AddProductCommand command) {
        return manipulateProduct.addProduct(command)
                .handle(
                        newProduct -> ResponseEntity.ok().build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    //Todo Security Admin
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<RestProduct> getAllProducts() {
        return manipulateProduct.getAllProducts();
    }

    //Todo Security Admin
    @PatchMapping("{id}")
    public void updateStatus(@RequestBody String value, @PathVariable Long id) {

    }

}
