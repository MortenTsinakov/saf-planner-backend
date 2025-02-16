package hr.adriaticanimation.saf_planner.controllers.label;

import hr.adriaticanimation.saf_planner.dtos.label.AttachLabelToFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.AttachLabelsToFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.label.CreateLabelRequest;
import hr.adriaticanimation.saf_planner.dtos.label.DeleteLabelResponse;
import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/labels")
    @Operation(description = "Fetch all labels for project with given id")
    public ResponseEntity<List<LabelResponse>> getAllLabelsForProject(@RequestParam("projectId") Long projectId) {
        return labelService.getAllLabelsForProject(projectId);
    }

    @PostMapping("/label")
    @Operation(description = "Create new label for project")
    public ResponseEntity<LabelResponse> createLabel(@Valid @RequestBody CreateLabelRequest request) {
        return labelService.createLabel(request);
    }

    @PostMapping("/fragment/label")
    @Operation(description = "Attach a label to a fragment")
    public ResponseEntity<LabelResponse> addLabelToFragment(@Valid @RequestBody AttachLabelToFragmentRequest request) {
        return labelService.attachLabelToFragment(request);
    }

    @PostMapping("/fragment/labels")
    @Operation(description = "Attach several labels to a fragment")
    public ResponseEntity<List<LabelResponse>> addLabelToFragmentAll(@Valid @RequestBody AttachLabelsToFragmentRequest request) {
        return labelService.attachLabelsToFragment(request);
    }

    @PutMapping(value = "/label", params = "id")
    @Operation(description = "Update label")
    public ResponseEntity<LabelResponse> updateLabel(@RequestParam("id") Long id, @Valid @RequestBody UpdateLabelRequest request) {
        return labelService.updateLabel(id, request);
    }

    @DeleteMapping(value = "label", params = "id")
    @Operation(description = "Delete label")
    public ResponseEntity<DeleteLabelResponse> deleteLabel(@RequestParam("id") Long id) {
        return labelService.deleteLabel(id);
    }

    @DeleteMapping(value = "/fragment/label", params = {"labelId", "fragmentId"})
    @Operation(description = "Remove label from fragment")
    public ResponseEntity<RemoveLabelFromFragmentResponse> removeLabelFromFragment(@RequestParam("labelId") Long labelId, @RequestParam("fragmentId") Long fragmentId) {
        return labelService.removeLabelFromFragment(labelId, fragmentId);
    }
}
