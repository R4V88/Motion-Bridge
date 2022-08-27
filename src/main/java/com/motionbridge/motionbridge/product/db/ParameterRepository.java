package com.motionbridge.motionbridge.product.db;

import com.motionbridge.motionbridge.product.entity.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    @Query(" SELECT p FROM Parameter p WHERE p.product.id = :productId ")
    List<Parameter> getAllParametersByProductId(long productId);
}
