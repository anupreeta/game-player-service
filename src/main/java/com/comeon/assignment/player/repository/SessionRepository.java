package com.comeon.assignment.player.repository;

import com.comeon.assignment.player.entity.Session;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Session> findByEmailAndLogoutTimeIsNull(String email);
    Optional<Session> findBySessionToken(String token);
    Optional<Session> findByLogoutTimeIsNull();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Session> findBySessionTokenAndLogoutTimeIsNull(String token);

}
