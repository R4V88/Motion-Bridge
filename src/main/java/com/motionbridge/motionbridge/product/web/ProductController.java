package com.motionbridge.motionbridge.product.web;

import com.motionbridge.motionbridge.product.application.ActiveProduct;
import com.motionbridge.motionbridge.product.application.port.ProductUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {

    final ProductUseCase product;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ActiveProduct> getActiveProducts() {
        return product.getActiveProducts();
    }
}
