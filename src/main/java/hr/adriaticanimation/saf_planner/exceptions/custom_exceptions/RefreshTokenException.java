package hr.adriaticanimation.saf_planner.exceptions.custom_exceptions;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String message) {
        super(message);
    }
}
