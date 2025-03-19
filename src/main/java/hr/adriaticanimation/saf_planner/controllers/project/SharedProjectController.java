package hr.adriaticanimation.saf_planner.controllers.project;

import hr.adriaticanimation.saf_planner.dtos.fragment.SharedProjectFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.project.ShareProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.SharedProjectResponse;
import hr.adriaticanimation.saf_planner.services.project.SharedProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/projects/shared")
@RequiredArgsConstructor
public class SharedProjectController {

    private final SharedProjectService sharedProjectService;

    @GetMapping(params = "projectId")
    @Operation(description = "Fetch shared project with given id")
    public ResponseEntity<SharedProjectResponse> getSharedProject(@RequestParam("projectId") Long projectId) {
        return sharedProjectService.getSharedProjectById(projectId);
    }

    @GetMapping(value = "/fragments", params = "projectId")
    @Operation(description = "Fetch all fragments for shared project with given id")
    public ResponseEntity<List<SharedProjectFragmentResponse>> getSharedProjectFragments(@RequestParam("projectId") Long projectId) {
        return sharedProjectService.getSharedProjectFragments(projectId);
    }

    @GetMapping
    @Operation(description = "Fetch all project shared with the user")
    public ResponseEntity<List<SharedProjectResponse>> getAllSharedProjects() {
        return sharedProjectService.getSharedProjects();
    }

    @PostMapping
    @Operation(description = "Share project with another user")
    public ResponseEntity<SharedProjectResponse> shareProject(@Valid @RequestBody ShareProjectRequest request) {
        return sharedProjectService.shareProject(request);
    }
}
