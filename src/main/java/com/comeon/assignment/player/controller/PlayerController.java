package com.comeon.assignment.player.controller;

import com.comeon.assignment.player.model.LoginRequest;
import com.comeon.assignment.player.model.PlayerRequest;
import com.comeon.assignment.player.model.TimeLimitRequest;
import com.comeon.assignment.player.service.PlayerService;
import com.comeon.assignment.player.service.SessionService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;
    @Autowired
    private SessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPlayer(@RequestBody PlayerRequest playerRequest) {
        playerService.register(playerRequest);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token;
        try {
             token = sessionService.login(loginRequest);
             //TODO: can try out error response pretty format
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        return ResponseEntity.ok(Map.of("sessionToken", token));
    }

    @PostMapping("/limit")
    // limit is in minutes
    public ResponseEntity<?> setLimit(@RequestBody TimeLimitRequest timeLimitRequest) {
        try {
            playerService.setTimeLimit(timeLimitRequest.email(), timeLimitRequest.limit());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Session-Token") String token) {
        try {
            sessionService.logout(token);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok("Logged out");
    }
}
