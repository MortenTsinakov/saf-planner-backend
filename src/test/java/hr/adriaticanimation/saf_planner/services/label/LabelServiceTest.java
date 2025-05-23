package hr.adriaticanimation.saf_planner.services.label;

import hr.adriaticanimation.saf_planner.dtos.label.AttachLabelToFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.AttachLabelsToFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.CreateLabelRequest;
import hr.adriaticanimation.saf_planner.dtos.label.DeleteLabelResponse;
import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
        Long labelId = 1L;
        UpdateLabelRequest request = new UpdateLabelRequest(
                "Description", "#000000"
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
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.labelToLabelResponse(label)).thenReturn(lr);

        ResponseEntity<LabelResponse> response = labelService.updateLabel(labelId, request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Description", label.getDescription());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
        verify(labelRepository).save(label);
        verify(labelMapper).labelToLabelResponse(label);
    }

    @Test
    void testUpdateLabelColorSuccess() {
        Long labelId = 1L;
        UpdateLabelRequest request = new UpdateLabelRequest(
                "Description", "#111111"
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
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.labelToLabelResponse(label)).thenReturn(lr);

        ResponseEntity<LabelResponse> response = labelService.updateLabel(labelId, request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("#111111", label.getColor());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
        verify(labelRepository).save(label);
        verify(labelMapper).labelToLabelResponse(label);
    }

    @Test
    void testUpdateLabelNotFound() {
        User user = new User();
        Long labelId = 1L;
        UpdateLabelRequest request = new UpdateLabelRequest( "Description", "#000000");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.updateLabel(labelId, request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testUpdateLabelProjectNotOwnedByUser() {
        Long labelId = 1L;
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user2).build();
        Label label = Label.builder().project(project).build();
        UpdateLabelRequest request = new UpdateLabelRequest( "Description", "#000000");

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));

        assertThrows(ResourceNotFoundException.class, () -> labelService.updateLabel(labelId, request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
        verifyNoMoreInteractions(labelRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testDeleteLabelSuccess() {
        Long labelId = 1L;
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();
        List<Label> labelList = new ArrayList<>();
        labelList.add(label);
        project.setLabels(labelList);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));

        ResponseEntity<DeleteLabelResponse> response = labelService.deleteLabel(labelId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(project.getLabels().isEmpty());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
        verify(labelRepository).deleteById(labelId);
    }

    @Test
    void testDeleteLabelNotFound() {
        Long labelId = 1L;
        User user = User.builder().id(1L).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.deleteLabel(labelId));
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
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
        Long labelId = 1L;
        Long fragmentId = 1L;
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();
        Fragment fragment = Fragment.builder().id(1L).project(project).build();
        LabelInFragmentId labelInFragmentId = new LabelInFragmentId(label.getId(), fragment.getId());

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(fragmentId)).thenReturn(Optional.of(fragment));

        ResponseEntity<RemoveLabelFromFragmentResponse> response = labelService.removeLabelFromFragment(labelId, fragmentId);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        verify(labelRepository).getLabelById(labelId);
        verify(fragmentRepository).getFragmentById(fragmentId);
        verify(labelInFragmentRepository).deleteById(labelInFragmentId);
    }

    @Test
    void testRemoveLabelFromFragmentLabelNotFound() {
        Long labelId = 1L;
        Long fragmentId = 1L;
        User user = User.builder().id(1L).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(labelId, fragmentId));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
        verifyNoInteractions(labelInFragmentRepository);
    }

    @Test
    void testRemoveLabelFromFragmentFragmentNotFound() {
        Long labelId = 1L;
        Long fragmentId = 1L;
        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Label label = Label.builder().id(1L).project(project).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(fragmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(labelId, fragmentId));

        verify(labelRepository).getLabelById(labelId);
        verify(fragmentRepository).getFragmentById(fragmentId);
        verifyNoInteractions(labelInFragmentRepository);
    }

    @Test
    void testRemoveLabelFromFragmentProjectNotOwnedByUserFragmentCheck() {
        Long labelId = 1L;
        Long fragmentId = 1L;
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user).build();
        Project project2 = Project.builder().id(2L).owner(user2).build();
        Label label = Label.builder().id(1L).project(project).build();
        Fragment fragment = Fragment.builder().id(1L).project(project2).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));
        when(fragmentRepository.getFragmentById(fragmentId)).thenReturn(Optional.of(fragment));

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(labelId, fragmentId));

        verify(labelRepository).getLabelById(labelId);
        verify(fragmentRepository).getFragmentById(fragmentId);
        verifyNoInteractions(labelInFragmentRepository);
    }

    @Test
    void testRemoveLabelFromFragmentProjectNotOwnedByUserLabelCheck() {
        Long labelId = 1L;
        Long fragmentId = 1L;
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(user2).build();
        Label label = Label.builder().id(1L).project(project).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(labelRepository.getLabelById(labelId)).thenReturn(Optional.of(label));

        assertThrows(ResourceNotFoundException.class, () -> labelService.removeLabelFromFragment(labelId, fragmentId));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(labelRepository).getLabelById(labelId);
        verifyNoInteractions(labelInFragmentRepository);
    }

    @Test
    void testAttachLabelsToFragmentSuccess() {
        List<Long> labelIds = List.of(1L, 2L, 3L);
        Long fragmentId = 1L;
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(labelIds, fragmentId);

        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .id(1L)
                .owner(user)
                .build();
        Fragment fragment = Fragment.builder()
                .id(1L)
                .project(project)
                .build();
        Label l1 = Label.builder()
                .id(1L)
                .project(project)
                .build();
        Label l2 = Label.builder()
                .id(1L)
                .project(project)
                .build();
        Label l3 = Label.builder()
                .id(1L)
                .project(project)
                .build();

        when(labelRepository.findLabelsByIdIsIn(labelIds)).thenReturn(List.of(l1, l2, l3));
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(fragmentId)).thenReturn(Optional.of(fragment));

        ResponseEntity<List<LabelResponse>> response = labelService.attachLabelsToFragment(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(labelRepository).findLabelsByIdIsIn(labelIds);
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(fragmentId);
        verify(labelInFragmentRepository).saveAll(any());
        verify(labelInFragmentRepository).saveAll(anyList());
        verify(labelMapper).labelsToLabelResponses(anyList());
    }

    @Test
    void testAttachLabelsToFragmentLabelsIsEmptyInRequest() {
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(List.of(), 1L);
        ResponseEntity<List<LabelResponse>> response = labelService.attachLabelsToFragment(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        verify(labelRepository).findLabelsByIdIsIn(List.of());
        verifyNoInteractions(authenticationService);
        verifyNoInteractions(fragmentRepository);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testAttachLabelsToFragmentLabelsIsEmptyAfterDatabaseQuery() {
        List<Long> labelIds = List.of(1L, 2L, 3L);
        Long fragmentId = 1L;
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(labelIds, fragmentId);

        ResponseEntity<List<LabelResponse>> response = labelService.attachLabelsToFragment(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        verify(labelRepository).findLabelsByIdIsIn(labelIds);
        verifyNoInteractions(authenticationService);
        verifyNoInteractions(fragmentRepository);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testAttachLabelsToFragmentFragmentNotFound() {
        List<Long> labelIds = List.of(1L, 2L, 3L);
        User user = User.builder().id(1L).build();
        Long fragmentId = 1L;
        Label l1 = Label.builder()
                .id(1L)
                .build();
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(labelIds, fragmentId);

        when(labelRepository.findLabelsByIdIsIn(labelIds)).thenReturn(List.of(l1));
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(fragmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labelService.attachLabelsToFragment(request));

        verify(labelRepository).findLabelsByIdIsIn(labelIds);
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(fragmentId);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testAttachLabelsToFragmentFragmentNotOwnedByUser() {
        User user = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder().id(2L).build();
        Project project = Project.builder()
                .id(1L)
                .owner(user2)
                .build();
        Fragment fragment = Fragment.builder()
                .id(1L)
                .project(project)
                .build();
        Label l1 = Label.builder()
                .id(1L)
                .project(project)
                .build();
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(List.of(1L, 2L), 1L);
        List<Label> labels = List.of(l1);

        when(labelRepository.findLabelsByIdIsIn(List.of(1L, 2L))).thenReturn(labels);
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(1L)).thenReturn(Optional.of(fragment));

        assertThrows(ResourceNotFoundException.class, () -> labelService.attachLabelsToFragment(request));

        verify(labelRepository).findLabelsByIdIsIn(List.of(1L, 2L));
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(1L);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }

    @Test
    void testAttachLabelsToFragmentLabelNotInProject() {
        User user = User.builder()
                .id(1L)
                .build();
        Project project = Project.builder()
                .id(1L)
                .owner(user)
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .owner(user)
                .build();
        Fragment fragment = Fragment.builder()
                .id(1L)
                .project(project)
                .build();
        Label l1 = Label.builder()
                .id(1L)
                .project(project)
                .build();
        Label l2 = Label.builder()
                .id(2L)
                .project(project2)
                .build();

        List<Long> labelIds = List.of(1L, 2L);
        Long fragmentId = 1L;
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(labelIds, fragmentId);
        List<Label> labels = List.of(l1, l2);

        when(labelRepository.findLabelsByIdIsIn(labelIds)).thenReturn(labels);
        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(fragmentId)).thenReturn(Optional.of(fragment));

        assertThrows(ResourceNotFoundException.class, () -> labelService.attachLabelsToFragment(request));

        verify(labelRepository).findLabelsByIdIsIn(labelIds);
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(fragmentId);
        verifyNoInteractions(labelInFragmentRepository);
        verifyNoInteractions(labelMapper);
    }
}