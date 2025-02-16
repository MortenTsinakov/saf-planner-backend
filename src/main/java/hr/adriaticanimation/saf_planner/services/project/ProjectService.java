package hr.adriaticanimation.saf_planner.services.project;

import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectRequest;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.project.ProjectMapper;
import hr.adriaticanimation.saf_planner.repositories.project.ProjectRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final AuthenticationService authenticationService;

    public ResponseEntity<ProjectResponse> getProjectById(Long projectId) {
        Project project = getUserProjectById(projectId);
        ProjectResponse response = projectMapper.projectToProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        User user = authenticationService.getUserFromSecurityContextHolder();
        List<Project> projectList = projectRepository.getProjectsByOwnerOrderByUpdatedAtDesc(user);
        List<ProjectResponse> response = projectList
                .stream()
                .map(projectMapper::projectToProjectResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ProjectResponse> createNewProject(CreateProjectRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Project project = projectMapper.createProjectRequestToProject(request, user);
        project = projectRepository.save(project);
        ProjectResponse response = projectMapper.projectToProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ProjectResponse> updateProject(Long id, UpdateProjectRequest request) {
        Project project = getUserProjectById(id);

        request.title().ifPresent(project::setTitle);
        request.description().ifPresent(project::setDescription);
        request.estimatedLengthInSeconds().ifPresent(project::setEstimatedLengthInSeconds);

        project.setUpdatedAt(Timestamp.from(Instant.now()));

        project = projectRepository.save(project);

        ProjectResponse response = projectMapper.projectToProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<DeleteProjectResponse> deleteProject(Long projectId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Project project = projectRepository.getProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectRepository.delete(project);
        DeleteProjectResponse response = new DeleteProjectResponse(projectId, "Project was successfully deleted");
        return ResponseEntity.ok(response);
    }

    public Project getUserProjectById(Long projectId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        return projectRepository.getProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}
