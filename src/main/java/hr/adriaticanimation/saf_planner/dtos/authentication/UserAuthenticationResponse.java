package hr.adriaticanimation.saf_planner.dtos.authentication;

public record UserAuthenticationResponse(
        Long id,
        String email,
        String firstName,
        String lastName
) {}
