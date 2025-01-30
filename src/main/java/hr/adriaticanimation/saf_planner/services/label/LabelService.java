package hr.adriaticanimation.saf_planner.services.label;

import hr.adriaticanimation.saf_planner.dtos.label.CreateLabelRequest;
import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.dtos.label.UpdateLabelColorRequest;
import hr.adriaticanimation.saf_planner.dtos.label.UpdateLabelDescriptionRequest;
import hr.adriaticanimation.saf_planner.dtos.label.UpdateLabelRequest;
import hr.adriaticanimation.saf_planner.entities.label.Label;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.label.LabelMapper;
import hr.adriaticanimation.saf_planner.repositories.label.LabelRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final ProjectService projectService;
    private final AuthenticationService authenticationService;

    public ResponseEntity<List<LabelResponse>> getAllLabelsForProject(Long projectId) {
        Project project = projectService.getUserProjectById(projectId);
        List<Label> labels = labelRepository.findAllByProject(project);
        List<LabelResponse> response = labelMapper.labelsToLabelResponses(labels);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<LabelResponse> createLabel(@Valid CreateLabelRequest request) {
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

    public <T extends UpdateLabelRequest> ResponseEntity<LabelResponse> updateLabel(T request, Consumer<Label> updateFunction) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Label label = labelRepository.getLabelById(request.getLabelId())
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        if (!label.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Label not found");
        }
        updateFunction.accept(label);
        label = labelRepository.save(label);

        LabelResponse response = labelMapper.labelToLabelResponse(label);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<LabelResponse> updateLabelDescription(@Valid UpdateLabelDescriptionRequest request) {
        return updateLabel(request, label -> label.setDescription(request.getDescription()));
    }

    public ResponseEntity<LabelResponse> updateLabelColor(@Valid UpdateLabelColorRequest request) {
        return updateLabel(request, label -> label.setColor(request.getColor()));
    }
}
