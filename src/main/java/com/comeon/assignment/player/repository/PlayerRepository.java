package com.comeon.assignment.player.repository;

import com.comeon.assignment.player.entity.Player;
import com.comeon.assignment.player.model.PlayerRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface PlayerRepository extends JpaRepository<Player, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Player findByEmailAndPassword(String email, String password);
}
