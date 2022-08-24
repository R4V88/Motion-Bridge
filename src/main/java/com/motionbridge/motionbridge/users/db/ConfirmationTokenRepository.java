package com.motionbridge.motionbridge.users.db;

import com.motionbridge.motionbridge.users.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    void deleteByUserEntityId(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    Integer updateConfirmedAt(String token,
                              LocalDateTime confirmedAt);

    ConfirmationToken findConfirmationTokenByUserEntity_Id(Long id);
}
