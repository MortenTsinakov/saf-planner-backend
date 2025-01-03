package hr.adriaticanimation.saf_planner.controllers.project;

import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectDescriptionRequest;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectEstimatedLengthRequest;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectTitleRequest;
import hr.adriaticanimation.saf_planner.services.project.ProjectService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(description = "Fetch all user projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping
    @Operation(description = "Create new project")
    public ResponseEntity<ProjectResponse> createNewProject(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.createNewProject(request);
    }

    @PutMapping("/title")
    @Operation(description = "Update project title")
    public ResponseEntity<ProjectResponse> updateProjectTitle(@Valid @RequestBody UpdateProjectTitleRequest request) {
        return projectService.updateProjectTitle(request);
    }

    @PutMapping("/description")
    @Operation(description = "Update project description")
    public ResponseEntity<ProjectResponse> updateProjectDescription(@Valid @RequestBody UpdateProjectDescriptionRequest request) {
        return projectService.updateProjectDescription(request);
    }

    @PutMapping("/estimated-length")
    @Operation(description = "Update project estimated length (in seconds)")
    public ResponseEntity<ProjectResponse> updateProjectEstimatedLength(@Valid @RequestBody UpdateProjectEstimatedLengthRequest request) {
        return projectService.updateProjectEstimatedLength(request);
    }

    @DeleteMapping
    @Operation(description = "Delete project with given id")
    public ResponseEntity<DeleteProjectResponse> deleteProject(@RequestBody DeleteProjectRequest request) {
        return projectService.deleteProject(request);
    }
}
