package com.motionbridge.motionbridge.product.db;

import com.motionbridge.motionbridge.product.entity.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    @Query(" SELECT p FROM Presentation p WHERE p.product.id = :productId ")
    List<Presentation> getAllPresentationsByProductId(long productId);
}
