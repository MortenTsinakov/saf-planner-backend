package hr.adriaticanimation.saf_planner.controllers.screenplay;

import hr.adriaticanimation.saf_planner.dtos.screenplay.CreateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.DeleteScreenplayResponse;
import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayExportData;
import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayResponse;
import hr.adriaticanimation.saf_planner.dtos.screenplay.UpdateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.UpdateScreenplayResponse;
import hr.adriaticanimation.saf_planner.services.screenplay.ScreenplayService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<ScreenplayResponse> createScreenplay(@Valid @RequestBody CreateScreenplayRequest request) {
        return screenplayService.createScreenplay(request);
    }

    @PutMapping
    @Operation(description = "Update screenplay with given id")
    public ResponseEntity<UpdateScreenplayResponse> updateScreenplay(@Valid @RequestBody UpdateScreenplayRequest request) {
        return screenplayService.updateScreenplay(request);
    }

    @DeleteMapping(params = "id")
    @Operation(description = "Delete screenplay with given id")
    public ResponseEntity<DeleteScreenplayResponse> deleteScreenplay(@RequestParam("id") Long id) {
        return screenplayService.deleteScreenplay(id);
    }

    @PostMapping(path = "/export")
    @Operation(description = "Export screenplay with given id as PDF")
    public ResponseEntity<byte[]> exportScreenplay(@RequestBody ScreenplayExportData data) {
        return screenplayService.exportScreenplay(data);
    }
}
