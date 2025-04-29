package hr.adriaticanimation.saf_planner.dtos.account;

import hr.adriaticanimation.saf_planner.utils.validators.OptionalNotBlank;

import java.util.Optional;

public record UsernameUpdateRequest(
        @OptionalNotBlank(message = "First name can't be blank")
        Optional<String> firstName,
        @OptionalNotBlank(message = "Last name can't be blank")
        Optional<String> lastName
) {}
