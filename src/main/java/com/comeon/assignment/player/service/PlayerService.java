package com.comeon.assignment.player.service;

import com.comeon.assignment.player.entity.Player;
import com.comeon.assignment.player.exceptions.InvalidCredentialsException;
import com.comeon.assignment.player.exceptions.PlayerNotFoundException;
import com.comeon.assignment.player.model.LoginRequest;
import com.comeon.assignment.player.model.PlayerRequest;
import com.comeon.assignment.player.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public void register(PlayerRequest playerRequest) {
        Player player = new Player(playerRequest.email(),
                playerRequest.password(),
                playerRequest.name(),
                playerRequest.surname(),
                playerRequest.dob(),
                playerRequest.address(),
                null,
                null);
        playerRepository.save(player);
    }

    public void setTimeLimit(String email, Integer timeLimit) {
        Player player = playerRepository.findById(email).orElseThrow(() -> PlayerNotFoundException.playerNotFound(email));
        if (player.getSessionStart() == null) {
            throw new RuntimeException("Inactive player");
        }
        player.setTimeLimit(TimeUnit.MINUTES.toMillis(timeLimit.intValue()));
        playerRepository.save(player);
    }
}
