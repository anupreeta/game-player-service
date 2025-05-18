package com.comeon.assignment.player.model;

import jakarta.validation.constraints.NotNull;

public record TimeLimitRequest(
        @NotNull
        String email,
        Integer limit
) {
}
