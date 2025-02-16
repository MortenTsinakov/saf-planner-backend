package hr.adriaticanimation.saf_planner.controllers.project;

import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectRequest;
import hr.adriaticanimation.saf_planner.services.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(params = "id")
    @Operation(description = "Fetch project with given id")
    public ResponseEntity<ProjectResponse> getProjectById(@RequestParam("id") Long id) {
        return projectService.getProjectById(id);
    }

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

    @PatchMapping(params = "id")
    @Operation(description = "Update project field")
    public ResponseEntity<ProjectResponse> updateProject(@RequestParam("id") Long id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping(params = "id")
    @Operation(description = "Delete project with given id")
    public ResponseEntity<DeleteProjectResponse> deleteProject(@RequestParam("id") Long id) {
        return projectService.deleteProject(id);
    }
}
