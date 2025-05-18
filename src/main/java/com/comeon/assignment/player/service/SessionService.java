package com.comeon.assignment.player.service;

import com.comeon.assignment.player.entity.Player;
import com.comeon.assignment.player.entity.Session;
import com.comeon.assignment.player.exceptions.InvalidCredentialsException;
import com.comeon.assignment.player.exceptions.PlayerNotFoundException;
import com.comeon.assignment.player.model.LoginRequest;
import com.comeon.assignment.player.repository.PlayerRepository;
import com.comeon.assignment.player.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public String login(LoginRequest loginRequest){
        // Validate player's email and password
        Player player = playerRepository.findByEmailAndPassword(loginRequest.email(), loginRequest.password());
        if (player == null) {
            throw new IllegalArgumentException("Invalid credentials supplied");
        }
        player.setSessionStart(LocalDateTime.now());
        playerRepository.save(player);

/*        if (player.getTimeLimit() != null && player.getSessionStart() != null) {
            long duration = java.time.Duration.between(player.getSessionStart(), LocalDateTime.now()).toMillis();
            if (duration > player.getTimeLimit()) throw new RuntimeException("Player's daily time limit has exceeded");
        }
        player.setSessionStart(LocalDateTime.now());
        playerRepository.save(player);*/

/*        Optional<Session> activeSession = sessionRepository.findByEmailAndLogoutTimeIsNull(loginRequest.email());
        if (activeSession.isPresent()){
            throw new IllegalStateException("Player is already logged in");
        }*/

        sessionRepository.findByEmailAndLogoutTimeIsNull(loginRequest.email())
                .ifPresent(session -> {
            throw new IllegalStateException("Player is already logged in"); });

        String token = UUID.randomUUID().toString();
        Session session =  new Session(player.getEmail(), token, LocalDateTime.now());
        sessionRepository.save(session);

        // 3. Enforce daily time limit
        Long limit = player.getTimeLimit();
        if (limit != null && player.getSessionStart() != null) {
            // sum of today's sessions duration
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            var allSessions = sessionRepository.findAll().stream().toList();
            var activeSessionsOfPlayer = sessionRepository.findAll().stream()
                    .filter(s -> s.getEmail().equals(player.getEmail()))
                    .filter(s -> s.getLoginTime().isAfter(startOfDay))
                    .toList();
            var sessionDurations = sessionRepository.findAll().stream()
                    .filter(s -> s.getEmail().equals(player.getEmail()))
                    .filter(s -> s.getLoginTime().isAfter(startOfDay))
                    .mapToLong(s -> Duration.between(s.getLoginTime(),
                            s.getLogoutTime() != null ? s.getLogoutTime() : LocalDateTime.now()).toMillis())
                    .toArray();
            var usedToday = sessionRepository.findAll().stream()
                    .filter(s -> s.getEmail().equals(player.getEmail()))
                    .filter(s -> s.getLoginTime().isAfter(startOfDay))
                    .mapToLong(s -> Duration.between(s.getLoginTime(),
                            s.getLogoutTime() != null ? s.getLogoutTime() : LocalDateTime.now()).toMillis())
                    .sum();
            if (usedToday >= limit) {
                session.setLogoutTime(LocalDateTime.now());
                throw new IllegalStateException("Login failed. Daily time limit reached");
            }
        }
        return token;
    }

    @Transactional
    public void logout(String sessionId) {
        Session session = sessionRepository.findBySessionTokenAndLogoutTimeIsNull(sessionId).orElseThrow(() ->
                new IllegalArgumentException("Invalid or expired session token"));
        session.setLogoutTime(LocalDateTime.now());
        sessionRepository.save(session);
        Player player = playerRepository.findById(session.getEmail()).orElseThrow(() ->
                PlayerNotFoundException.playerNotFound(session.getEmail()));
        player.setSessionStart(null);
        playerRepository.save(player);

/*        {
            session.setLogoutTime(LocalDateTime.now());
            playerRepository.findById(session.getEmail()).ifPresent(player -> { player.setSessionStart(null); });
        });
        throw new IllegalArgumentException("Invalid session id supplied");*/
    }
}
