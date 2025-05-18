package com.comeon.assignment.player.service;

import com.comeon.assignment.player.entity.Player;
import com.comeon.assignment.player.entity.Session;
import com.comeon.assignment.player.model.LoginRequest;
import com.comeon.assignment.player.repository.PlayerRepository;
import com.comeon.assignment.player.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private PlayerRepository playerRepository;

    private static String email = "joe@gmail.com";
    private static String password = "password";

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoginSuccessfullyAndReturnSessionToken() {
        // given
        Player player = getPlayer();

        // when
        when(playerRepository.findByEmailAndPassword(email, password)).thenReturn(player);
        when(sessionRepository.findByEmailAndLogoutTimeIsNull(email)).thenReturn(Optional.empty());
        when(sessionRepository.save(ArgumentMatchers.any())).thenAnswer(invocation-> invocation.getArgument(0));

        LoginRequest loginRequest = new LoginRequest(email, password);
        var result = sessionService.login(loginRequest);

        assertNotNull(result);
    }

    @Test
    void shouldFailLoginWhenWrongUsername() {
        // given
        getPlayer();

        // when
        when(playerRepository.findByEmailAndPassword("invalid", password)).thenReturn(null);

        LoginRequest loginRequest = new LoginRequest("invalid", password);
        assertThrows( IllegalArgumentException.class, () -> sessionService.login(loginRequest));
    }

    @Test
    void shouldFailLoginWhenWrongPassword() {
        // given
        getPlayer();

        // when
        when(playerRepository.findByEmailAndPassword(email, "invalid")).thenReturn(null);

        LoginRequest loginRequest = new LoginRequest(email, "invalid");
        assertThrows( IllegalArgumentException.class, () -> sessionService.login(loginRequest));
    }

    @Test
    void shouldFailLoginForActivePlayer() {
        // given
        Player player = getPlayer();

        Session existingSession = new Session();
        existingSession.setEmail(player.getEmail());

        // when
        when(playerRepository.findByEmailAndPassword(email, password)).thenReturn(player);
        when(sessionRepository.findByEmailAndLogoutTimeIsNull(email)).thenReturn(Optional.of(existingSession));

        // then
        assertThrows(IllegalStateException.class, () -> sessionService.login(new LoginRequest(email, password)));
    }

    @Test
    void shouldFailWhenDailyLimitReached() {
        // given
        Player player = getPlayer();
        player.setTimeLimit(60000L); //1minute = 60,000 milliseconds

        Session pastSession = new Session();
        pastSession.setEmail(player.getEmail());
        pastSession.setLoginTime(LocalDateTime.now().minusMinutes(2));
        pastSession.setLogoutTime(LocalDateTime.now().minusMinutes(1));

        when(playerRepository.findByEmailAndPassword(email, password)).thenReturn(player);
        when(sessionRepository.findByEmailAndLogoutTimeIsNull(email)).thenReturn(Optional.empty());
        //when(sessionRepository.findByEmailAndLogoutTimeIsNull(email)).thenReturn(Optional.of(pastSession));
        when(sessionRepository.findAll()).thenReturn(List.of(pastSession));

        assertThrows(IllegalStateException.class, () -> sessionService.login(new LoginRequest(email, password)));
    }

    @Test
    void shouldLogoutSuccessfully() {
        // given
        Player player = getPlayer();
        player.setSessionStart(LocalDateTime.now());

        String token = "65f23dea-d405-4338-9bda-295d7e79f8e9";
        Session session = new Session();
        session.setEmail(player.getEmail());
        session.setSessionToken(token);
        session.setLoginTime(LocalDateTime.now());

        when(sessionRepository.findBySessionTokenAndLogoutTimeIsNull(token)).thenReturn(Optional.of(session));
        when(sessionRepository.save(ArgumentMatchers.any())).thenAnswer(invocation-> invocation.getArgument(0));
        when(playerRepository.findById(session.getEmail())).thenReturn(Optional.of(player));

        // when
        sessionService.logout(token);

        // then
        assertNotNull(session.getLogoutTime());
        assertNull(player.getSessionStart());

        verify(sessionRepository, times(1)).save(session);
        verify(playerRepository, times(1)).save(player);
    }

    private static Player getPlayer() {
        Player player = new Player();
        player.setEmail(email);
        player.setPassword(password);
        return player;
    }
}
