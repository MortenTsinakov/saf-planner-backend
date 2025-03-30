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
import org.hibernate.sql.Update;
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
        ProjectResponse response = new ProjectResponse(null, null, null, null, null, null, null, null, null);

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
                List.of(),
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
                null, null, null, null, null, null, null, null, null
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
        Long projectId = 1L;
        String title = "New Title";
        User user = new User();
        Project project = new Project();
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.of(title),
                Optional.empty(),
                Optional.empty()
        );
        ProjectResponse response = new ProjectResponse(
                1L, "Title", null, null, null, null, null, null, null
        );

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.updateProject(projectId, request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(projectId, user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(title, project.getTitle());
        assertNotNull(project.getUpdatedAt());
    }

    @Test
    void testUpdateProjectTitleProjectNotFound() {
        Long projectId = 1L;
        User user = new User();
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.of("Title"),
                Optional.empty(),
                Optional.empty()
        );

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(1L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(projectId, request));

        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(projectMapper);
    }

    @Test
    void testUpdateProjectDescription() {
        Long projectId = 1L;
        String description = "New Description";
        User user = new User();
        Project project = new Project();
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.empty(),
                Optional.of(description),
                Optional.empty()
        );
        ProjectResponse response = new ProjectResponse(1L, "Description", null, null, null, null, null, null, null);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.updateProject(projectId, request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(projectId, user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(description, project.getDescription());
        assertNotNull(project.getUpdatedAt());
    }

    @Test
    void testUpdateProjectEstimatedLength() {
        Long projectId = 1L;
        Integer estimatedLengthInSeconds = 120;
        User user = new User();
        Project project = new Project();
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.empty(),
                Optional.empty(),
                Optional.of(estimatedLengthInSeconds)
        );
        ProjectResponse response = new ProjectResponse(1L, null, null, null, null, null, null, null, null);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.updateProject(projectId, request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(projectId, user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(estimatedLengthInSeconds, project.getEstimatedLengthInSeconds());
        assertNotNull(project.getUpdatedAt());
    }

    @Test
    void testDeleteProject() {
        Long projectId = 1L;
        User user = new User();
        Project project = new Project();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.of(project));

        ResponseEntity<DeleteProjectResponse> result = projectService.deleteProject(projectId);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(projectId, user);
        verify(projectRepository).delete(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        assertNotNull(result.getBody().projectId());
        assertEquals(projectId, result.getBody().projectId());
    }

    @Test
    void testDeleteProjectProjectNotFound() {
        Long projectId = 1L;
        User user = new User();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(projectId));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(projectId, user);
        verifyNoMoreInteractions(projectRepository);
    }
}