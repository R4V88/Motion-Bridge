package com.motionbridge.motionbridge.product.db;

import com.motionbridge.motionbridge.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(" SELECT p.name, p.price FROM Product p WHERE p.isActive = true ")
    List<Product> getAllProducts();

    List<Product> getProductsByIsActiveIsTrue();
}
