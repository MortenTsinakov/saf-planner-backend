package hr.adriaticanimation.saf_planner.services.account;

import hr.adriaticanimation.saf_planner.dtos.account.DeleteAccountRequest;
import hr.adriaticanimation.saf_planner.dtos.account.DeleteAccountResponse;
import hr.adriaticanimation.saf_planner.dtos.account.PasswordUpdateRequest;
import hr.adriaticanimation.saf_planner.dtos.account.PasswordUpdateResponse;
import hr.adriaticanimation.saf_planner.dtos.account.UsernameUpdateRequest;
import hr.adriaticanimation.saf_planner.dtos.account.UsernameUpdateResponse;
import hr.adriaticanimation.saf_planner.dtos.authentication.SignOutResponse;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.repositories.user.UserRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.authentication.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    /**
     * Update user's first name, last name or both.
     * @param request - request containing the new first name, last name or both.
     * @return - response containing a success message.
     */
    public ResponseEntity<UsernameUpdateResponse> updateUsersName(@Valid UsernameUpdateRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();

        request.firstName().ifPresent(user::setFirstName);
        request.lastName().ifPresent(user::setLastName);

        userRepository.save(user);
        UsernameUpdateResponse response = new UsernameUpdateResponse("Name was successfully updated");
        return ResponseEntity.ok(response);
    }

    /**
     * Update user's password.
     * @param request - request containing the old password and the new password.
     * @return - response containing a success message.
     */
    public ResponseEntity<PasswordUpdateResponse> updateUsersPassword(@Valid PasswordUpdateRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        PasswordUpdateResponse response = new PasswordUpdateResponse("Password was successfully updated");

        return ResponseEntity.ok(response);
    }

    /**
     * Delete the user's account. Delete refresh token from the database and replace JWT and refresh token in
     * the cookies with ones that immediately expire.
     *
     * @param request - Request containing the user's password. The user's password is used to confirm the deletion.
     * @return - Response containing a success message.
     */
    public ResponseEntity<DeleteAccountResponse> deleteAccount(@Valid DeleteAccountRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Provided password is incorrect");
        }

        refreshTokenService.deleteUsersPreviousRefreshToken(user);
        HttpHeaders headers = authenticationService.createHeaders("", 0, "", 0);

        userRepository.delete(user);
        DeleteAccountResponse response = new DeleteAccountResponse("Account was successfully deleted");

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
