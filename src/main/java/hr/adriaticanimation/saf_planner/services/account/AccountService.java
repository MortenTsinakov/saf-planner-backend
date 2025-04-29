package hr.adriaticanimation.saf_planner.services.account;

import hr.adriaticanimation.saf_planner.dtos.account.PasswordUpdateRequest;
import hr.adriaticanimation.saf_planner.dtos.account.PasswordUpdateResponse;
import hr.adriaticanimation.saf_planner.dtos.account.UsernameUpdateRequest;
import hr.adriaticanimation.saf_planner.dtos.account.UsernameUpdateResponse;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.repositories.user.UserRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    public ResponseEntity<UsernameUpdateResponse> updateUsersName(@Valid UsernameUpdateRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();

        request.firstName().ifPresent(user::setFirstName);
        request.lastName().ifPresent(user::setLastName);

        userRepository.save(user);
        UsernameUpdateResponse response = new UsernameUpdateResponse("Name was successfully updated");
        return ResponseEntity.ok(response);
    }

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
}
