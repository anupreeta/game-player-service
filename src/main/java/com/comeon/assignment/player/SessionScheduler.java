package com.comeon.assignment.player;

import com.comeon.assignment.player.entity.Player;
import com.comeon.assignment.player.entity.Session;
import com.comeon.assignment.player.repository.PlayerRepository;
import com.comeon.assignment.player.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class SessionScheduler {

    @Autowired
    private SessionRepository sessionRepo;
    @Autowired private PlayerRepository playerRepo;



    @Scheduled(fixedRate = 120_000) // every 2 min
    public void autoLogoutExpiredSessions() {
        Optional<Session> activeSessions = sessionRepo.findByLogoutTimeIsNull();
        activeSessions.ifPresent(session -> {
            Player player = playerRepo.findById(session.getEmail()).orElse(null);
            if (player != null && player.getTimeLimit() != null) {

                long activeSeconds = Duration.between(session.getLoginTime(), LocalDateTime.now()).toMillis();
                if (activeSeconds > player.getTimeLimit()) {
                    session.setLogoutTime(LocalDateTime.now());
                    sessionRepo.save(session);
                    System.out.println("Logged out: " + session.getEmail() + " due to time limit");
                }
            }
        });
    }
}
