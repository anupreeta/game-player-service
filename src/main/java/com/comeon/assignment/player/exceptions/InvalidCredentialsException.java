package com.comeon.assignment.player.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    private final String email;
    private final String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public InvalidCredentialsException(String message, String email, String password) {
        super(message);
        this.email = email;
        this.password = password;
    }
}
