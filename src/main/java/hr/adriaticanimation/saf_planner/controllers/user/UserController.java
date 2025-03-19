package hr.adriaticanimation.saf_planner.controllers.user;

import hr.adriaticanimation.saf_planner.dtos.user.UserSearchResponse;
import hr.adriaticanimation.saf_planner.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    @Operation(description = "Search for a user by their full name")
    public ResponseEntity<List<UserSearchResponse>> searchForUser(@RequestParam String query) {
        return userService.searchUser(query);
    }
}
