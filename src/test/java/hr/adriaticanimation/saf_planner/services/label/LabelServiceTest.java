package hr.adriaticanimation.saf_planner.services.label;

import hr.adriaticanimation.saf_planner.dtos.label.AttachLabelToFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.CreateLabelRequest;
import hr.adriaticanimation.saf_planner.dtos.label.DeleteLabelRequest;
import hr.adriaticanimation.saf_planner.dtos.label.DeleteLabelResponse;
import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.dtos.label.RemoveLabelFromFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.RemoveLabelFromFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.label.UpdateLabelRequest;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.label.Label;
import hr.adriaticanimation.saf_planner.entities.label.LabelInFragmentId;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.label.LabelMapper;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.repositories.label.LabelInFragmentRepository;
import hr.adriaticanimation.saf_planner.repositories.label.LabelRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock
    private LabelRepository labelRepository;
    @Mock
    private LabelInFragmentRepository labelInFragmentRepository;
    @Mock
    private LabelMapper labelMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private FragmentRepository fragmentRepository;
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private LabelService labelService;

    @Test
    void testGetAllLabelsForProjectSuccess() {
        Long projectId = 1L;
        Project project = Project.builder().build();
        Label l1 = new Label();
        Label l2 = new Label();
        List<Label> labels = List.of(l1, l2);
        LabelResponse lr1 = new LabelResponse(1L, null, "#000000");
        LabelResponse lr2 = new LabelResponse(2L, null, "#111111");
        List<LabelResponse> labelResponses = List.of(lr1, lr2);

        when(projectService.getUserProjectById(projectId)).thenReturn(project);
        when(labelRepository.findAllByProject(project)).thenReturn(labels);
        when(labelMapper.labelsToLabelResponses(labels)).thenReturn(labelResponses);

        ResponseEntity<List<LabelResponse>> response = labelService.getAllLabelsForProject(projectId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(projectService).getUserProjectById(projectId);
        verify(labelRepository).findAllByProject(project);
        verify(labelMapper).labelsToLabelResponses(labels);
    }

    @Test
    void testCreateLabelSuccess() {
        CreateLabelRequest request = new CreateLabelRequest(1L, "Description", "#000000");
        Project project = new Project();
        LabelResponse labelResponse = new LabelResponse(1L, "Description", "#111111");

        when(projectService.getUserProjectById(request.projectId())).thenReturn(project);
        when(labelRepository.save(any())).thenReturn(new Label());
        when(labelMapper.labelToLabelResponse(any())).thenReturn(labelResponse);

        ResponseEntity<LabelResponse> response = labelService.createLabel(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(projectService).getUserProjectById(request.projectId());
        verify(labelMapper).labelToLabelResponse(any(Label.class));
        verify(labelRepository).save(any(Label.class));
    }

    @Test
    void testUpdateLabelDescriptionSuccess() {
        UpdateLabelRequest request = new UpdateLabelRequest(
                1L, "Description", "#000000"
        );
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder()
                .id(1L)
                .project(project)
                .color("#000000")
                .description("Old Description")
                .build();
        LabelResponse lr = new LabelResponse(1L, "Description", "#111111");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.labelToLabelResponse(label)).thenReturn(lr);

        ResponseEntity<LabelResponse> response = labelService.updateLabel(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Description", label.getDescription());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verify(labelRepository).save(label);
        verify(labelMapper).labelToLabelResponse(label);
    }

    @Test
    void testUpdateLabelColorSuccess() {
        UpdateLabelRequest request = new UpdateLabelRequest(
                1L, "Description", "#111111"
        );
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder()
                .id(1L)
                .project(project)
                .color("#000000")
                .description("Old Description")
                .build();
        LabelResponse lr = new LabelResponse(1L, "Description", "#111111");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.labelToLabelResponse(label)).thenReturn(lr);

        ResponseEntity<LabelResponse> response = labelService.updateLabel(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("#111111", label.getColor());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verify(labelRepository).save(label);
        verify(labelMapper).labelToLabelResponse(label);
    }

    @Test
    void testUpdateLabelNotFound() {
        User user = new User();
        UpdateLabelRequest request = new UpdateLabelRequest(1L, "Description", "#000000");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.updateLabel(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testUpdateLabelProjectNotOwnedByUser() {
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user2).build();
        Label label = Label.builder().project(project).build();
        UpdateLabelRequest request = new UpdateLabelRequest(1L, "Description", "#000000");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));

        assertThrows(ResourceNotFoundException.class, () -> labelService.updateLabel(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testDeleteLabelSuccess() {
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();

        DeleteLabelRequest request = new DeleteLabelRequest(1L);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));

        ResponseEntity<DeleteLabelResponse> response = labelService.deleteLabel(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verify(labelRepository).deleteById(request.labelId());
    }

    @Test
    void testDeleteLabelNotFound() {
        User user = User.builder().id(1L).build();
        DeleteLabelRequest request = new DeleteLabelRequest(1L);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.deleteLabel(request));
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verifyNoMoreInteractions(labelRepository);
    }

    @Test
    void testAttachLabelToFragmentSuccess() {
        AttachLabelToFragmentRequest request = new AttachLabelToFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();
        Fragment fragment = Fragment.builder().id(1L).project(project).build();
        LabelResponse labelResponse = new LabelResponse(1L, "Description", "#000000");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));
        when(labelMapper.labelToLabelResponse(label)).thenReturn(labelResponse);

        ResponseEntity<LabelResponse> response = labelService.attachLabelToFragment(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        verify(labelRepository).getLabelById(request.labelId());
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verify(labelMapper).labelToLabelResponse(label);
        verify(labelInFragmentRepository).save(any());
    }

    @Test
    void testAttachLabelToFragmentLabelNotFound() {
        AttachLabelToFragmentRequest request = new AttachLabelToFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.attachLabelToFragment(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testAttachLabelToFragmentFragmentNotFound() {
        AttachLabelToFragmentRequest request = new AttachLabelToFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.attachLabelToFragment(request));

        verify(labelRepository).getLabelById(request.labelId());
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testAttachLabelToFragmentProjectNotOwnedByUserFragmentCheck() {
        AttachLabelToFragmentRequest request = new AttachLabelToFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Project project2 = Project.builder().id(2L).owner(user2).build();
        Label label = Label.builder().id(1L).project(project).build();
        Fragment fragment = Fragment.builder().id(1L).project(project2).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));

        assertThrows(ResourceNotFoundException.class, () -> labelService.attachLabelToFragment(request));

        verify(labelRepository).getLabelById(request.labelId());
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testAttachLabelToFragmentProjectNotOwnedByUserLabelCheck() {
        AttachLabelToFragmentRequest request = new AttachLabelToFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user2).build();
        Label label = Label.builder().id(1L).project(project).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));

        assertThrows(ResourceNotFoundException.class, () -> labelService.attachLabelToFragment(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testRemoveLabelFromFragmentSuccess() {
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();
        Fragment fragment = Fragment.builder().id(1L).project(project).build();
        LabelInFragmentId labelInFragmentId = new LabelInFragmentId(label.getId(), fragment.getId());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));

        ResponseEntity<RemoveLabelFromFragmentResponse> response = labelService.removeLabelFromFragment(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        verify(labelRepository).getLabelById(request.labelId());
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verify(labelInFragmentRepository).deleteById(labelInFragmentId);
    }

    @Test
    void testRemoveLabelFromFragmentLabelNotFound() {
        User user = User.builder().id(1L).build();
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(1L, 1L);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verifyNoInteractions(labelInFragmentRepository);
    }

    @Test
    void testRemoveLabelFromFragmentFragmentNotFound() {
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(request));

        verify(labelRepository).getLabelById(request.labelId());
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verifyNoInteractions(labelInFragmentRepository);
    }

    @Test
    void testRemoveLabelFromFragmentProjectNotOwnedByUserFragmentCheck() {
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Project project2 = Project.builder().id(2L).owner(user2).build();
        Label label = Label.builder().id(1L).project(project).build();
        Fragment fragment = Fragment.builder().id(1L).project(project2).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(request));

        verify(labelRepository).getLabelById(request.labelId());
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verifyNoInteractions(labelInFragmentRepository);
    }

    @Test
    void testRemoveLabelFromFragmentProjectNotOwnedByUserLabelCheck() {
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(1L, 1L);
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user2).build();
        Label label = Label.builder().id(1L).project(project).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(request.labelId())).thenReturn(Optional.of(label));

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(request.labelId());
        verifyNoInteractions(labelInFragmentRepository);
    }

}