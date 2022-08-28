package com.motionbridge.motionbridge.users.db;

import com.motionbridge.motionbridge.users.entity.UserEntity;
import com.motionbridge.motionbridge.users.web.mapper.RichRestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String username);

    Optional<UserEntity> findUserById(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity a " +
            "SET a.isVerified = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);
}
