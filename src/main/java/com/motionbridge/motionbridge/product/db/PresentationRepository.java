package com.motionbridge.motionbridge.product.db;

import com.motionbridge.motionbridge.product.entity.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
}
