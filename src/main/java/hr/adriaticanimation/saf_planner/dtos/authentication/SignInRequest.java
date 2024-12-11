package hr.adriaticanimation.saf_planner.dtos.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record SignInRequest(
        @NotBlank(message = "Email was not provided")
        @Email(message = "Provided email was not in correct format")
        String email,
        @NotEmpty(message = "Password cannot be empty")
        String password
) {
}
