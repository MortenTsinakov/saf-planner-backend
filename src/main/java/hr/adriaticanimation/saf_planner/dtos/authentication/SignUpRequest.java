package hr.adriaticanimation.saf_planner.dtos.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Email was not provided")
        @Email(message = "Provided email was not in correct format")
        String email,
        @NotBlank(message = "First name cannot be blank")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        String lastName,
        @Size(min = 8)
        @NotEmpty(message = "Password was not provided")
        String password
) {}
