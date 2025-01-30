package hr.adriaticanimation.saf_planner.services.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.MoveFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentDuration;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentLongDescription;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentOnTimelineStatus;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentShortDescription;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.fragment.FragmentMapper;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.repositories.project.ProjectRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FragmentServiceTest {

    @Mock
    private FragmentRepository fragmentRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private FragmentMapper fragmentMapper;
    @InjectMocks
    private FragmentService fragmentService;

    @Test
    void testGetAllFragmentsForProjectSuccess() {
        User user = new User();
        Long projectId = 1L;
        Project project = new Project();
        Fragment f1 = new Fragment();
        Fragment f2 = new Fragment();
        FragmentResponse fr1 = new FragmentResponse(null, null, null, null, true, null, null, null);
        FragmentResponse fr2 = new FragmentResponse(null, null, null, null, true, null, null, null);
        List<Fragment> fragments = List.of(f1, f2);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.of(project));
        when(fragmentRepository.findByProjectOrderByPositionAsc(project)).thenReturn(fragments);
        when(fragmentMapper.fragmentToFragmentResponse(f1)).thenReturn(fr1);
        when(fragmentMapper.fragmentToFragmentResponse(f2)).thenReturn(fr2);

        ResponseEntity<List<FragmentResponse>> response = fragmentService.getAllFragmentsForProject(projectId);

        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(projectId, user);
        verify(fragmentRepository).findByProjectOrderByPositionAsc(project);
        verify(fragmentMapper).fragmentToFragmentResponse(f1);
    }

    @Test
    void testGetAllFragmentsForProjectNotFound() {
        Long projectId = 1L;
        User user = new User();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(projectId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fragmentService.getAllFragmentsForProject(projectId));
        verifyNoMoreInteractions(authenticationService);
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(fragmentRepository);
        verifyNoInteractions(fragmentMapper);
    }

    @Test
    void testCreateFragmentSuccess() {
        User user = new User();
        Project project = Project.builder()
                .id(1L)
                .build();
        CreateFragmentRequest request = new CreateFragmentRequest(
                "short",
                "long",
                15,
                true,
                10,
                1L
        );
        FragmentResponse response = new FragmentResponse(1L, "short", "long", 15, true, 10, 1L, List.of());
        Fragment fragment = Fragment.builder()
                .id(1L)
                .position(10)
                .build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(projectRepository.getProjectByIdAndOwner(request.projectId(), user)).thenReturn(Optional.of(project));
        when(fragmentMapper.createFragmentRequestToFragment(request, project)).thenReturn(fragment);
        when(fragmentRepository.save(fragment)).thenReturn(fragment);
        when(fragmentMapper.fragmentToFragmentResponse(fragment)).thenReturn(response);

        ResponseEntity<FragmentResponse> result = fragmentService.createFragment(request);

        assertNotNull(result);
        assertTrue(result.getStatusCode().is2xxSuccessful());

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(projectRepository).getProjectByIdAndOwner(request.projectId(), user);
        verify(fragmentMapper).createFragmentRequestToFragment(request, project);
        verify(fragmentRepository).shiftFragmentPositionsForward(project.getId(), fragment.getPosition());
        verify(fragmentRepository).save(fragment);
        verify(projectRepository).save(project);
        verify(fragmentMapper).fragmentToFragmentResponse(fragment);

        assertNotNull(project.getUpdatedAt());
    }

    @Test
    void testUpdateFragmentShortDescriptionSuccess() throws NoSuchFieldException, IllegalAccessException {
        UpdateFragmentShortDescription request = new UpdateFragmentShortDescription();
        Field shortField = request.getClass().getDeclaredField("shortDescription");
        shortField.setAccessible(true);
        shortField.set(request, "short");
        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .owner(user)
                .build();
        Fragment fragment = Fragment.builder()
                .project(project)
                .build();
        FragmentResponse response = new FragmentResponse(1L, "short", "long", 15, true, 10, 1L, List.of());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.getFragmentId())).thenReturn(Optional.of(fragment));
        when(fragmentRepository.save(fragment)).thenReturn(fragment);
        when(projectRepository.save(project)).thenReturn(project);
        when(fragmentMapper.fragmentToFragmentResponse(fragment)).thenReturn(response);

        fragmentService.updateFragmentShortDescription(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(request.getFragmentId());
        verify(fragmentRepository).save(fragment);
        verify(projectRepository).save(project);
        verify(fragmentMapper).fragmentToFragmentResponse(fragment);

        assertEquals(request.getShortDescription(), fragment.getShortDescription());
        assertNotNull(project.getUpdatedAt());

    }

    @Test
    void testUpdateFragmentLongDescriptionSuccess() throws NoSuchFieldException, IllegalAccessException {
        UpdateFragmentLongDescription request = new UpdateFragmentLongDescription();
        Field shortField = request.getClass().getDeclaredField("longDescription");
        shortField.setAccessible(true);
        shortField.set(request, "long");
        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .owner(user)
                .build();
        Fragment fragment = Fragment.builder()
                .project(project)
                .build();
        FragmentResponse response = new FragmentResponse(1L, "short", "long", 15, true, 10, 1L, List.of());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.getFragmentId())).thenReturn(Optional.of(fragment));
        when(fragmentRepository.save(fragment)).thenReturn(fragment);
        when(projectRepository.save(project)).thenReturn(project);
        when(fragmentMapper.fragmentToFragmentResponse(fragment)).thenReturn(response);

        fragmentService.updateFragmentLongDescription(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(request.getFragmentId());
        verify(fragmentRepository).save(fragment);
        verify(projectRepository).save(project);
        verify(fragmentMapper).fragmentToFragmentResponse(fragment);

        assertEquals(request.getLongDescription(), fragment.getLongDescription());
        assertNotNull(project.getUpdatedAt());

    }

    @Test
    void testUpdateFragmentDurationSuccess() throws NoSuchFieldException, IllegalAccessException {
        UpdateFragmentDuration request = new UpdateFragmentDuration();
        Field shortField = request.getClass().getDeclaredField("durationInSeconds");
        shortField.setAccessible(true);
        shortField.set(request, 15);
        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .owner(user)
                .build();
        Fragment fragment = Fragment.builder()
                .project(project)
                .build();
        FragmentResponse response = new FragmentResponse(1L, "short", "long", 15, true, 10, 1L, List.of());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.getFragmentId())).thenReturn(Optional.of(fragment));
        when(fragmentRepository.save(fragment)).thenReturn(fragment);
        when(projectRepository.save(project)).thenReturn(project);
        when(fragmentMapper.fragmentToFragmentResponse(fragment)).thenReturn(response);

        fragmentService.updateFragmentDuration(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(request.getFragmentId());
        verify(fragmentRepository).save(fragment);
        verify(projectRepository).save(project);
        verify(fragmentMapper).fragmentToFragmentResponse(fragment);

        assertEquals(request.getDurationInSeconds(), fragment.getDurationInSeconds());
        assertNotNull(project.getUpdatedAt());

    }

    @Test
    void testUpdateFragmentOnTimelineSuccess() throws NoSuchFieldException, IllegalAccessException {
        UpdateFragmentOnTimelineStatus request = new UpdateFragmentOnTimelineStatus();
        Field shortField = request.getClass().getDeclaredField("onTimeline");
        shortField.setAccessible(true);
        shortField.set(request, false);
        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .owner(user)
                .build();
        Fragment fragment = Fragment.builder()
                .project(project)
                .build();
        FragmentResponse response = new FragmentResponse(1L, "short", "long", 15, true, 10, 1L, List.of());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.getFragmentId())).thenReturn(Optional.of(fragment));
        when(fragmentRepository.save(fragment)).thenReturn(fragment);
        when(projectRepository.save(project)).thenReturn(project);
        when(fragmentMapper.fragmentToFragmentResponse(fragment)).thenReturn(response);

        fragmentService.updateFragmentOnTimelineStatus(request);

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(request.getFragmentId());
        verify(fragmentRepository).save(fragment);
        verify(projectRepository).save(project);
        verify(fragmentMapper).fragmentToFragmentResponse(fragment);

        assertEquals(request.isOnTimeline(), fragment.isOnTimeline());
        assertNotNull(project.getUpdatedAt());

    }

    @Test
    void testUpdateFragmentShortDescriptionFragmentNotFound() {
        UpdateFragmentShortDescription request = new UpdateFragmentShortDescription();
        User user = User.builder()
                .id(1L)
                .build();
        Consumer<Fragment> updateAction = f -> f.setShortDescription(request.getShortDescription());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.getFragmentId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fragmentService.updateFragment(request, updateAction));

        verifyNoMoreInteractions(authenticationService);
        verifyNoMoreInteractions(fragmentRepository);
        verifyNoInteractions(projectRepository);
        verifyNoInteractions(fragmentMapper);

    }

    @Test
    void testUpdateFragmentShortDescriptionProjectOwnerIsNotTheUser() {
        UpdateFragmentShortDescription request = new UpdateFragmentShortDescription();
        User user = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .build();
        Project project = Project.builder()
                .owner(user2)
                .build();
        Fragment fragment = Fragment.builder()
                .project(project)
                .build();
        Consumer<Fragment> updateAction = f -> f.setShortDescription(request.getShortDescription());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.getFragmentId())).thenReturn(Optional.of(fragment));

        assertThrows(ResourceNotFoundException.class, () -> fragmentService.updateFragment(request, updateAction));

        verifyNoMoreInteractions(authenticationService);
        verifyNoMoreInteractions(fragmentRepository);
        verifyNoInteractions(projectRepository);
        verifyNoInteractions(fragmentMapper);

    }

    @Test
    void testDeleteFragmentSuccess() {
        DeleteFragmentRequest request = new DeleteFragmentRequest(3L);
        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .id(2L)
                .owner(user)
                .build();
        Fragment fragment = Fragment.builder()
                .id(3L)
                .position(10)
                .project(project)
                .build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));

        ResponseEntity<DeleteFragmentResponse> response = fragmentService.deleteFragment(request);

        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(project.getUpdatedAt());

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verify(fragmentRepository).shiftFragmentPositionsBackward(project.getId(), fragment.getPosition());
        verify(fragmentRepository).delete(fragment);
        verify(projectRepository).save(project);
    }

    @Test
    void testDeleteFragmentNotFound() {
        DeleteFragmentRequest request = new DeleteFragmentRequest(3L);
        User user = new User();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fragmentService.deleteFragment(request));

        verifyNoMoreInteractions(authenticationService);
        verifyNoMoreInteractions(fragmentRepository);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void testDeleteFragmentProjectOwnerIsNotTheUser() {
        DeleteFragmentRequest request = new DeleteFragmentRequest(3L);
        User user = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .build();
        Project project = Project.builder()
                .owner(user2)
                .build();
        Fragment fragment = Fragment.builder()
                .project(project)
                .build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));

        assertThrows(ResourceNotFoundException.class, () -> fragmentService.deleteFragment(request));
        verifyNoMoreInteractions(authenticationService);
        verifyNoMoreInteractions(fragmentRepository);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void testMoveFragmentBackwardSuccess() {
        User owner = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .id(1L)
                .owner(owner)
                .build();
        Fragment fragment = Fragment.builder()
                .id(1L)
                .project(project)
                .position(5)
                .build();
        MoveFragmentRequest request = new MoveFragmentRequest(1L, 1);
        FragmentResponse response = new FragmentResponse(1L, "", "", 1, true, 1, project.getId(), List.of());

        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(owner);
        when(fragmentRepository.save(fragment)).thenReturn(fragment);
        when(fragmentMapper.fragmentToFragmentResponse(fragment)).thenReturn(response);

        ResponseEntity<FragmentResponse> result = fragmentService.moveFragment(request);

        assertTrue(result.getStatusCode().is2xxSuccessful());

        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).shiftFragmentPositionsForward(project.getId(), 1, 5);
        verify(projectRepository).save(project);
        verify(fragmentRepository).save(fragment);
        verify(fragmentMapper).fragmentToFragmentResponse(fragment);
    }

    @Test
    void testMoveFragmentForwardSuccess() {
        User owner = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .id(1L)
                .owner(owner)
                .build();
        Fragment fragment = Fragment.builder()
                .id(1L)
                .project(project)
                .position(2)
                .build();
        MoveFragmentRequest request = new MoveFragmentRequest(1L, 7);
        FragmentResponse response = new FragmentResponse(1L, "", "", 1, true, 7, project.getId(), List.of());

        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(owner);
        when(fragmentRepository.save(fragment)).thenReturn(fragment);
        when(fragmentMapper.fragmentToFragmentResponse(fragment)).thenReturn(response);

        ResponseEntity<FragmentResponse> result = fragmentService.moveFragment(request);

        assertTrue(result.getStatusCode().is2xxSuccessful());

        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).shiftFragmentPositionsBackward(project.getId(), 2, 7);
        verify(projectRepository).save(project);
        verify(fragmentRepository).save(fragment);
        verify(fragmentMapper).fragmentToFragmentResponse(fragment);
    }

    @Test
    void testMoveFragmentNotFound() {
        MoveFragmentRequest request = new MoveFragmentRequest(1L, 7);

        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fragmentService.moveFragment(request));

        verifyNoMoreInteractions(fragmentRepository);
        verifyNoInteractions(authenticationService);
        verifyNoInteractions(projectRepository);
        verifyNoInteractions(fragmentMapper);
    }

    @Test
    void testMoveFragmentProjectOwnerIsNotTheUser() {
        User user = User.builder()
                .id(1L)
                .build();
        User owner = User.builder()
                .id(2L)
                .build();
        Project project = Project.builder()
                .owner(owner)
                .id(1L)
                .build();
        Fragment fragment = Fragment.builder()
                .id(1L)
                .project(project)
                .position(5)
                .build();
        MoveFragmentRequest request = new MoveFragmentRequest(1L, 1);

        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);

        assertThrows(ResourceNotFoundException.class, () -> fragmentService.moveFragment(request));

        verifyNoMoreInteractions(fragmentRepository);
        verifyNoInteractions(projectRepository);
        verifyNoInteractions(fragmentMapper);
    }

    @Test
    void testMoveFragmentAlreadyInRequestedPosition() {
        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .owner(user)
                .id(1L)
                .build();
        Fragment fragment = Fragment.builder()
                .id(1L)
                .project(project)
                .position(5)
                .build();
        MoveFragmentRequest request = new MoveFragmentRequest(1L, 5);

        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> fragmentService.moveFragment(request));

        verifyNoMoreInteractions(fragmentRepository);
        verifyNoInteractions(projectRepository);
        verifyNoInteractions(fragmentMapper);


    }
}