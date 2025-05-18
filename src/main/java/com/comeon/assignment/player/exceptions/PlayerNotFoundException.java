package com.comeon.assignment.player.exceptions;

import java.text.MessageFormat;

public class PlayerNotFoundException extends RuntimeException {
    private PlayerNotFoundException(String message) {
        super(message);
    }

    public static PlayerNotFoundException playerNotFound(String email) {
        //return new PlayerNotFoundException("Player with email " +  email + " not found");
        return new PlayerNotFoundException(MessageFormat.format("Player with email {0} not found",  email));
    }
}
