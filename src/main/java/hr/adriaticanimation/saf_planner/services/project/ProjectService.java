package hr.adriaticanimation.saf_planner.services.project;

import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectDescriptionRequest;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectEstimatedLengthRequest;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectTitleRequest;
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

    public ResponseEntity<ProjectResponse> updateProjectTitle(UpdateProjectTitleRequest request) {
        Project project = getUserProjectById(request.projectId());

        project.setTitle(request.title());
        project.setUpdatedAt(Timestamp.from(Instant.now()));

        project = projectRepository.save(project);
        ProjectResponse response = projectMapper.projectToProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ProjectResponse> updateProjectDescription(UpdateProjectDescriptionRequest request) {
        Project project = getUserProjectById(request.projectId());

        project.setDescription(request.description());
        project.setUpdatedAt(Timestamp.from(Instant.now()));

        project = projectRepository.save(project);
        ProjectResponse response = projectMapper.projectToProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ProjectResponse> updateProjectEstimatedLength(UpdateProjectEstimatedLengthRequest request) {
        Project project = getUserProjectById(request.projectId());

        project.setEstimatedLengthInSeconds(request.estimatedLengthInSeconds());
        project.setUpdatedAt(Timestamp.from(Instant.now()));

        project = projectRepository.save(project);
        ProjectResponse response = projectMapper.projectToProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<DeleteProjectResponse> deleteProject(DeleteProjectRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Project project = projectRepository.getProjectByIdAndOwner(request.projectId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectRepository.delete(project);
        DeleteProjectResponse response = new DeleteProjectResponse(request.projectId(), "Project was successfully deleted");
        return ResponseEntity.ok(response);
    }

    private Project getUserProjectById(Long projectId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        return projectRepository.getProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}
