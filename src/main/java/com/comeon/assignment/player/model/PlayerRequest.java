package com.comeon.assignment.player.model;

import jakarta.validation.constraints.NotEmpty;

public record PlayerRequest(
        @NotEmpty
        String email,
        String password,
        String name,
        String surname,
        String dob,
        String address

) {
}
