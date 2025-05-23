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
import hr.adriaticanimation.saf_planner.entities.label.LabelInFragment;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for label operations
 */
@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelInFragmentRepository labelInFragmentRepository;
    private final LabelMapper labelMapper;
    private final ProjectService projectService;
    private final FragmentRepository fragmentRepository;
    private final AuthenticationService authenticationService;

    /**
     * Get all labels for a project with given id.
     *
     * @param projectId - id of the project for which to fetch the labels
     * @return - List of labels associated with the project
     */
    public ResponseEntity<List<LabelResponse>> getAllLabelsForProject(Long projectId) {
        Project project = projectService.getUserProjectById(projectId);
        List<Label> labels = labelRepository.findAllByProject(project);
        List<LabelResponse> response = labelMapper.labelsToLabelResponses(labels);
        return ResponseEntity.ok(response);
    }

    /**
     * Create new label for the project.
     *
     * @param request for creating a new label
     * @return - created label
     */
    public ResponseEntity<LabelResponse> createLabel(CreateLabelRequest request) {
        Project project = projectService.getUserProjectById(request.projectId());
        Label label = Label.builder()
                .project(project)
                .description(request.description())
                .color(request.color())
                .build();
        label = labelRepository.save(label);
        LabelResponse response = labelMapper.labelToLabelResponse(label);
        return ResponseEntity.ok(response);
    }

    /**
     * Update label.
     *
     * @param request - request for updating the label
     * @return - updated label
     */
    public ResponseEntity<LabelResponse> updateLabel(Long labelId, UpdateLabelRequest request) {
        Label label = getLabelById(labelId);
        label.setDescription(request.description());
        label.setColor(request.color());
        label = labelRepository.save(label);

        LabelResponse response = labelMapper.labelToLabelResponse(label);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<DeleteLabelResponse> deleteLabel(Long id) {
        Label label = getLabelById(id);
        Project project = label.getProject();

        if (project != null) {
            project.getLabels().remove(label);
        }

        labelRepository.deleteById(label.getId());
        DeleteLabelResponse response = new DeleteLabelResponse(label.getId(), "Label deleted");
        return ResponseEntity.ok(response);
    }

    /**
     * Helper function that fetches a label with given id from
     * the database and checks whether the label belongs to a project
     * owned by the user making the request.
     *
     * @param labelId - id of the label
     * @return - fetched label
     */
    private Label getLabelById(Long labelId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Label label = labelRepository.getLabelById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        if (!label.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Label not found");
        }
        return label;
    }

    /**
     * Helper function that fetches a fragment with given id from
     * the database and checks whether the fragment belongs to a project
     * owned by the user making the request.
     *
     * @param fragmentId - id of the fragment
     * @return - fetched fragment
     */
    private Fragment getFragmentById(Long fragmentId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Fragment fragment = fragmentRepository.getFragmentById(fragmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Fragment not found"));
        if (!fragment.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Fragment not found");
        }
        return fragment;
    }

    /**
     * Attach a single label to a fragment.
     * @param request - request to attach a label to fragment
     * @return - Label that was attached to the fragment
     */
    @Transactional
    public ResponseEntity<LabelResponse> attachLabelToFragment(AttachLabelToFragmentRequest request) {
        Label label = getLabelById(request.labelId());
        Fragment fragment = getFragmentById(request.fragmentId());

        LabelInFragment labelInFragment = new LabelInFragment(label, fragment);

        labelInFragmentRepository.save(labelInFragment);
        LabelResponse response = labelMapper.labelToLabelResponse(label);
        return ResponseEntity.ok(response);
    }

    /**
     * Attach several labels to a fragment.
     * @param request - request to attach labels
     * @return - list of labels that were attached to the fragment
     */
    @Transactional
    public ResponseEntity<List<LabelResponse>> attachLabelsToFragment(AttachLabelsToFragmentRequest request) {
        List<Label> labels = labelRepository.findLabelsByIdIsIn(request.labelIds());
        if (labels.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        Fragment fragment = getFragmentById(request.fragmentId());

        if (!labels.stream().allMatch(label -> label.getProject().getId().equals(fragment.getProject().getId()))) {
            throw new ResourceNotFoundException("Project doesn't have requested labels");
        }

        List<LabelInFragment> labelInFragmentList = labels
                .stream()
                .map(label -> new LabelInFragment(label, fragment))
                .toList();

        labelInFragmentRepository.saveAll(labelInFragmentList);
        List<LabelResponse> response = labelMapper.labelsToLabelResponses(labels);

        return ResponseEntity.ok(response);
    }

    /**
     * Remove label from a fragment
     * @param labelId - id of the label to be removed
     * @param fragmentId - id of the fragment from which to remove the label
     * @return - success message in case of successful removal
     */
    @Transactional
    public ResponseEntity<RemoveLabelFromFragmentResponse> removeLabelFromFragment(Long labelId, Long fragmentId) {
        Label label = getLabelById(labelId);
        Fragment fragment = getFragmentById(fragmentId);

        labelInFragmentRepository.deleteById(new LabelInFragmentId(label.getId(), fragment.getId()));
        RemoveLabelFromFragmentResponse response = new RemoveLabelFromFragmentResponse(
                label.getId(),
                fragment.getId(),
                "Removed label from fragment"
        );

        return ResponseEntity.ok(response);
    }
}
