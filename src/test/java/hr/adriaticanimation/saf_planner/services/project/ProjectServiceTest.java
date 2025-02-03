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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void testGetProjectByIdSuccess() {
        Long projectId = 1L;
        User user = new User();
        Project project = new Project();
        ProjectResponse response = new ProjectResponse(null, null, null, null, null, null, null, null);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.of(project));
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.getProjectById(projectId);

        assertTrue(result.getStatusCode().is2xxSuccessful());

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(projectId, user);
        verify(projectMapper).projectToProjectResponse(project);

    }

    @Test
    void testGetProjectByIdProjectNotFound() {
        Long projectId = 1L;
        User user = new User();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(projectId));

        verifyNoInteractions(projectMapper);
    }

    @Test
    void testGetAllProjects() {
        User user = new User();
        Project project = new Project();
        Timestamp timestamp = Timestamp.valueOf("2024-12-30 12:00:00");
        ProjectResponse response = new ProjectResponse(
                1L,
                "Title",
                "Description",
                360,
                timestamp,
                timestamp,
                "John Doe",
                List.of()
                );
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectsByOwnerOrderByUpdatedAtDesc(user)).thenReturn(List.of(project));
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<List<ProjectResponse>> result = projectService.getAllProjects();

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectsByOwnerOrderByUpdatedAtDesc(user);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void testCreateNewProject() {
        User user = new User();
        CreateProjectRequest request = new CreateProjectRequest(
                "Title",
                "Description",
                60
        );
        Project project = new Project();
        ProjectResponse response = new ProjectResponse(
                null, null, null, null, null, null, null, null
        );

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectMapper.createProjectRequestToProject(request, user)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.createNewProject(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectMapper).createProjectRequestToProject(request, user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void testUpdateProjectTitle() {
        User user = new User();
        Project project = new Project();
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(
                1L,
                "Title"
        );
        ProjectResponse response = new ProjectResponse(
                1L, "Title", null, null, null, null, null, null
        );

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.updateProjectTitle(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(request.title(), project.getTitle());
        assertNotNull(project.getUpdatedAt());
    }

    @Test
    void testUpdateProjectTitleProjectNotFound() {
        User user = new User();
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(1L, "Title");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(1L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProjectTitle(request));

        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(projectMapper);
    }

    @Test
    void testUpdateProjectDescription() {
        User user = new User();
        Project project = new Project();
        UpdateProjectDescriptionRequest request = new UpdateProjectDescriptionRequest(1L, "Description");
        ProjectResponse response = new ProjectResponse(1L, "Description", null, null, null, null, null, null);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.updateProjectDescription(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(request.description(), project.getDescription());
        assertNotNull(project.getUpdatedAt());
    }

    @Test
    void testUpdateProjectEstimatedLength() {
        User user = new User();
        Project project = new Project();
        UpdateProjectEstimatedLengthRequest request = new UpdateProjectEstimatedLengthRequest(1L, 120);
        ProjectResponse response = new ProjectResponse(1L, null, null, null, null, null, null, null);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.updateProjectEstimatedLength(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(request.estimatedLengthInSeconds(), project.getEstimatedLengthInSeconds());
        assertNotNull(project.getUpdatedAt());
    }

    @Test
    void testDeleteProject() {
        DeleteProjectRequest request = new DeleteProjectRequest(1L);
        User user = new User();
        Project project = new Project();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.of(project));

        ResponseEntity<DeleteProjectResponse> result = projectService.deleteProject(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verify(projectRepository).delete(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        assertNotNull(result.getBody().projectId());
        assertEquals(result.getBody().projectId(), request.projectId());
    }

    @Test
    void testDeleteProjectProjectNotFound() {
        DeleteProjectRequest request = new DeleteProjectRequest(1L);
        User user = new User();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verifyNoMoreInteractions(projectRepository);
    }
}