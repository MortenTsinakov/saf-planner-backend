package hr.adriaticanimation.saf_planner.controllers.label;

import hr.adriaticanimation.saf_planner.dtos.label.AttachLabelToFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.AttachLabelsToFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.CreateLabelRequest;
import hr.adriaticanimation.saf_planner.dtos.label.DeleteLabelRequest;
import hr.adriaticanimation.saf_planner.dtos.label.DeleteLabelResponse;
import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.dtos.label.RemoveLabelFromFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.RemoveLabelFromFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.label.UpdateLabelRequest;
import hr.adriaticanimation.saf_planner.services.label.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/label")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    @Operation(description = "Fetch all labels for project with given id")
    public ResponseEntity<List<LabelResponse>> getAllLabelsForProject(@RequestParam("projectId") Long projectId) {
        return labelService.getAllLabelsForProject(projectId);
    }

    @PostMapping
    @Operation(description = "Create new label for project")
    public ResponseEntity<LabelResponse> createLabel(@Valid @RequestBody CreateLabelRequest request) {
        return labelService.createLabel(request);
    }

    @PostMapping("/fragment")
    @Operation(description = "Attach a label to a fragment")
    public ResponseEntity<LabelResponse> addLabelToFragment(@Valid @RequestBody AttachLabelToFragmentRequest request) {
        return labelService.attachLabelToFragment(request);
    }

    @PostMapping("/fragment/all")
    @Operation(description = "Attach several labels to a fragment")
    public ResponseEntity<List<LabelResponse>> addLabelToFragmentAll(@Valid @RequestBody AttachLabelsToFragmentRequest request) {
        return labelService.attachLabelsToFragment(request);
    }

    @PutMapping()
    @Operation(description = "Update label")
    public ResponseEntity<LabelResponse> updateLabel(@Valid @RequestBody UpdateLabelRequest request) {
        return labelService.updateLabel(request);
    }

    @DeleteMapping
    @Operation(description = "Delete label")
    public ResponseEntity<DeleteLabelResponse> deleteLabel(@Valid @RequestBody DeleteLabelRequest request) {
        return labelService.deleteLabel(request);
    }

    @DeleteMapping("/fragment")
    @Operation(description = "Remove label from fragment")
    public ResponseEntity<RemoveLabelFromFragmentResponse> removeLabelFromFragment(@Valid @RequestBody RemoveLabelFromFragmentRequest request) {
        return labelService.removeLabelFromFragment(request);
    }
}
