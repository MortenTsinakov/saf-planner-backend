package hr.adriaticanimation.saf_planner.controllers.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentRequest;
import hr.adriaticanimation.saf_planner.services.fragment.FragmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/fragments")
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

    @PatchMapping(params = "id")
    @Operation(description = "Update fragment field")
    public ResponseEntity<FragmentResponse> updateFragment(@RequestParam("id") Long id, @Valid @RequestBody UpdateFragmentRequest request) {
        return fragmentService.updateFragment(id, request);
    }

    @DeleteMapping(params = "id")
    @Operation(description = "Delete fragment with given id")
    public ResponseEntity<DeleteFragmentResponse> deleteFragment(@RequestParam("id") Long id) {
        return fragmentService.deleteFragment(id);
    }
}
