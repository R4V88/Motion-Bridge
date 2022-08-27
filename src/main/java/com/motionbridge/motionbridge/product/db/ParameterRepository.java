package com.motionbridge.motionbridge.product.db;

import com.motionbridge.motionbridge.product.entity.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {
}
