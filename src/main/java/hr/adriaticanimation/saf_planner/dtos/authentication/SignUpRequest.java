package hr.adriaticanimation.saf_planner.dtos.authentication;

public record SignUpRequest(
        String email,
        String firstName,
        String lastName,
        String password
) {}
