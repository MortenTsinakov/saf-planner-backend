package hr.adriaticanimation.saf_planner.controllers.screenplay;

import hr.adriaticanimation.saf_planner.dtos.screenplay.CreateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayResponse;
import hr.adriaticanimation.saf_planner.entities.screenplay.Screenplay;
import hr.adriaticanimation.saf_planner.services.screenplay.ScreenplayService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/screenplays")
@RequiredArgsConstructor
public class ScreenplayController {

    private final ScreenplayService screenplayService;

    @GetMapping(params = "projectId")
    @Operation(description = "Fetch screenplay for a project")
    public ResponseEntity<ScreenplayResponse> getScreenplayForProject(@RequestParam("projectId") Long id) {
        return screenplayService.getScreenplayForProject(id);
    }

    @PostMapping
    @Operation(description = "Save new screenplay to database")
    public ResponseEntity<ScreenplayResponse> createScreenplay(@RequestBody CreateScreenplayRequest screenplay) {
        return screenplayService.createScreenplay(screenplay);
    }
}
