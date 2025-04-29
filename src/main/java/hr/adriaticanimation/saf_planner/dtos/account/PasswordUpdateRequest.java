package hr.adriaticanimation.saf_planner.dtos.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequest(
        @NotBlank(message = "Current password was not provided")
        String oldPassword,
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @NotEmpty(message = "Password was not provided")
        String newPassword
) {}
