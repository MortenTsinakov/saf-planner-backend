package hr.adriaticanimation.saf_planner.services.project;

import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.DeleteProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.UpdateProjectRequest;
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
                "John Doe"
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
                null, null, null, null, null, null, null
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
    void testUpdateProject() {
        User user = new User();
        UpdateProjectRequest request = new UpdateProjectRequest(
                1L,
                "Title",
                "Description",
                60);
        Project project = new Project();
        ProjectResponse response = new ProjectResponse(
                null, null, null, null, null, null, null
        );

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.projectToProjectResponse(project)).thenReturn(response);

        ResponseEntity<ProjectResponse> result = projectService.updateProject(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verify(projectRepository).save(project);
        verify(projectMapper).projectToProjectResponse(project);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void testUpdateProjectNoProjectWithGivenId() {
        UpdateProjectRequest request = new UpdateProjectRequest(null, null, null, null);
        User user = new User();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verifyNoInteractions(projectMapper);
        verifyNoMoreInteractions(projectRepository);
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