package hr.adriaticanimation.saf_planner.dtos.account;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank(message = "Password was not provided")
        String password
) {}
