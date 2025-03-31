package hr.adriaticanimation.saf_planner.services.user;

import hr.adriaticanimation.saf_planner.dtos.user.UserSearchResponse;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.repositories.user.UserRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private static final double THRESHOLD = 0.3;  // Fuzzy search threshold

    public ResponseEntity<List<UserSearchResponse>> searchUser(String query) {
        if (query == null || query.isBlank() || query.length() < 3) {
            return ResponseEntity.badRequest().build();
        }

        User user = authenticationService.getUserFromSecurityContextHolder();
        List<User> results = userRepository.findByFullName(query, THRESHOLD);
        List<UserSearchResponse> response = results.stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .map(u -> new UserSearchResponse(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
