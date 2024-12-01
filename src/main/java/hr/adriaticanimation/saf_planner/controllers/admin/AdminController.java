package hr.adriaticanimation.saf_planner.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    @Operation(description = "Test endpoint for admin")
    public String test() {
        return "You reached admin endpoint";
    }
}
