package hr.adriaticanimation.saf_planner.controllers.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.MoveFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentDuration;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentLongDescription;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentOnTimelineStatus;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentShortDescription;
import hr.adriaticanimation.saf_planner.services.fragment.FragmentService;
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
@RequestMapping("/api/fragment")
@RequiredArgsConstructor
public class FragmentController {

    private final FragmentService fragmentService;

    @GetMapping
    @Operation(description = "Fetch all fragments for a project with given id")
    public ResponseEntity<List<FragmentResponse>> getAllFragmentsForProject(@RequestParam("projectId") Long projectId) {
        return fragmentService.getAllFragmentsForProject(projectId);
    }

    @PostMapping
    @Operation(description = "Create new fragment for project with given id")
    public ResponseEntity<FragmentResponse> createFragment(@Valid @RequestBody CreateFragmentRequest request) {
        return fragmentService.createFragment(request);
    }

    @PutMapping("/short-description")
    @Operation(description = "Update fragment's short description")
    public ResponseEntity<FragmentResponse> updateFragmentShortDescription(@Valid @RequestBody UpdateFragmentShortDescription request) {
        return fragmentService.updateFragmentShortDescription(request);
    }

    @PutMapping("/long-description")
    @Operation(description = "Update fragment's long description")
    public ResponseEntity<FragmentResponse> updateFragmentLongDescription(@Valid @RequestBody UpdateFragmentLongDescription request) {
        return fragmentService.updateFragmentLongDescription(request);
    }

    @PutMapping("/duration")
    @Operation(description = "Update fragment's duration")
    public ResponseEntity<FragmentResponse> updateFragmentDuration(@Valid @RequestBody UpdateFragmentDuration request) {
        return fragmentService.updateFragmentDuration(request);
    }

    @PutMapping("/on-timeline")
    @Operation(description = "Update fragment's on timeline status")
    public ResponseEntity<FragmentResponse> updateFragmentOnTimelineStatus(@Valid @RequestBody UpdateFragmentOnTimelineStatus request) {
        return fragmentService.updateFragmentOnTimelineStatus(request);
    }

    @PutMapping("/move")
    @Operation(description = "Drag and drop a fragment to a new position")
    public ResponseEntity<FragmentResponse> moveFragment(@Valid @RequestBody MoveFragmentRequest request) {
        return fragmentService.moveFragment(request);
    }

    @DeleteMapping
    @Operation(description = "Delete fragment with given id")
    public ResponseEntity<DeleteFragmentResponse> deleteFragment(@Valid @RequestBody DeleteFragmentRequest request) {
        return fragmentService.deleteFragment(request);
    }
}
