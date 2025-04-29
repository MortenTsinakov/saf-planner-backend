package hr.adriaticanimation.saf_planner.controllers.account;

import hr.adriaticanimation.saf_planner.dtos.account.DeleteAccountRequest;
import hr.adriaticanimation.saf_planner.dtos.account.DeleteAccountResponse;
import hr.adriaticanimation.saf_planner.dtos.account.PasswordUpdateRequest;
import hr.adriaticanimation.saf_planner.dtos.account.PasswordUpdateResponse;
import hr.adriaticanimation.saf_planner.dtos.account.UsernameUpdateRequest;
import hr.adriaticanimation.saf_planner.dtos.account.UsernameUpdateResponse;
import hr.adriaticanimation.saf_planner.services.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PatchMapping("/name")
    @Operation(description = "Update name")
    public ResponseEntity<UsernameUpdateResponse> updateUsersName(@Valid @RequestBody UsernameUpdateRequest request) {
        return accountService.updateUsersName(request);
    }

    @PatchMapping("/password")
    @Operation(description = "Update password")
    public ResponseEntity<PasswordUpdateResponse> updateUsersPassword(@Valid @RequestBody PasswordUpdateRequest request) {
        return accountService.updateUsersPassword(request);
    }

    @PostMapping("/delete")
    @Operation(description = "Delete account")
    public ResponseEntity<DeleteAccountResponse> deleteUser(@Valid @RequestBody DeleteAccountRequest request) {
        // TODO: Using a POST method for now because it's not recommended to add a request body to a DELETE method. Research more about this.
        return accountService.deleteAccount(request);
    }
}
