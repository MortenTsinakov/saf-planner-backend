package hr.adriaticanimation.saf_planner.controllers.authentication;

import hr.adriaticanimation.saf_planner.dtos.authentication.SignInRequest;
import hr.adriaticanimation.saf_planner.dtos.authentication.SignOutResponse;
import hr.adriaticanimation.saf_planner.dtos.authentication.SignUpRequest;
import hr.adriaticanimation.saf_planner.dtos.authentication.UserAuthenticationResponse;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    @Operation(description = "Create a new user account")
    public ResponseEntity<UserAuthenticationResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/sign-in")
    @Operation(description = "Sign the user in")
    public ResponseEntity<UserAuthenticationResponse> signIn(@Valid @RequestBody SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @PostMapping("/sign-out")
    @Operation(description = "Sign the user out")
    public ResponseEntity<SignOutResponse> signOut() {
        return authenticationService.signOut();
    }

    @PostMapping("/refresh-token")
    @Operation(description = "Refresh user's JSON Web Token")
    public ResponseEntity<UserAuthenticationResponse> refreshToken(HttpServletRequest request) {
        return authenticationService.refreshToken(request);
    }
}
