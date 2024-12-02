package hr.adriaticanimation.saf_planner.dtos.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record SignInRequest(
        @Email(message = "Provided email was not in correct format")
        String email,
        @NotEmpty(message = "Password cannot be empty")
        String password
) {
}
