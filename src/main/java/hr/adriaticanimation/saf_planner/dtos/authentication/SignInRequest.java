package hr.adriaticanimation.saf_planner.dtos.authentication;

public record SignInRequest(
        String email,
        String password
) {
}
