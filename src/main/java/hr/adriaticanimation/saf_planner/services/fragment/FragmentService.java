package hr.adriaticanimation.saf_planner.services.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentDuration;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentLongDescription;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentOnTimelineStatus;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentShortDescription;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.fragment.FragmentMapper;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.repositories.project.ProjectRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FragmentService {

    private final FragmentRepository fragmentRepository;
    private final AuthenticationService authenticationService;
    private final ProjectRepository projectRepository;
    private final FragmentMapper fragmentMapper;

    public ResponseEntity<List<FragmentResponse>> getAllFragmentsForProject(Long projectId) {
        Project project = getProjectById(projectId);
        List<Fragment> fragments = fragmentRepository.findByProjectOrderByPositionAsc(project);
        List<FragmentResponse> response = fragments.stream()
                .map(fragmentMapper::fragmentToFragmentResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<FragmentResponse> createFragment(CreateFragmentRequest request) {
        Project project = getProjectById(request.projectId());
        Fragment fragment = fragmentMapper.createFragmentRequestToFragment(request, project);
        fragmentRepository.shiftFragmentPositionsForward(project.getId(), fragment.getPosition());
        fragment = fragmentRepository.save(fragment);

        project.setUpdatedAt(Timestamp.from(Instant.now()));
        projectRepository.save(project);

        FragmentResponse response = fragmentMapper.fragmentToFragmentResponse(fragment);

        return ResponseEntity.ok(response);
    }

    private Project getProjectById(Long projectId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        return projectRepository.getProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @Transactional
    public <T extends UpdateFragmentRequest> ResponseEntity<FragmentResponse> updateFragment(T request, Consumer<Fragment> updateAction) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Fragment fragment = fragmentRepository.getFragmentById(request.getFragmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fragment not found"));
        Project project = fragment.getProject();
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Project not found");
        }

        updateAction.accept(fragment);
        project.setUpdatedAt(Timestamp.from(Instant.now()));

        fragmentRepository.save(fragment);
        projectRepository.save(project);

        FragmentResponse response = fragmentMapper.fragmentToFragmentResponse(fragment);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<FragmentResponse> updateFragmentShortDescription(UpdateFragmentShortDescription request) {
        return updateFragment(request, fragment -> fragment.setShortDescription(request.getShortDescription()));
    }

    @Transactional
    public ResponseEntity<FragmentResponse> updateFragmentLongDescription(UpdateFragmentLongDescription request) {
        return updateFragment(request, fragment -> fragment.setLongDescription(request.getLongDescription()));
    }

    @Transactional
    public ResponseEntity<FragmentResponse> updateFragmentDuration(UpdateFragmentDuration request) {
        return updateFragment(request, fragment -> fragment.setDurationInSeconds(request.getDurationInSeconds()));
    }

    @Transactional
    public ResponseEntity<FragmentResponse> updateFragmentOnTimelineStatus(@Valid UpdateFragmentOnTimelineStatus request) {
        return updateFragment(request, fragment -> fragment.setOnTimeline(request.isOnTimeline()));
    }

    @Transactional
    public ResponseEntity<DeleteFragmentResponse> deleteFragment(@Valid DeleteFragmentRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Fragment fragment = fragmentRepository.getFragmentById(request.fragmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fragment not found"));
        Project project = fragment.getProject();
        if (!user.getId().equals(project.getOwner().getId())) {
            throw new ResourceNotFoundException("Project not found");
        }

        fragmentRepository.shiftFragmentPositionsBackward(project.getId(), fragment.getPosition());
        project.setUpdatedAt(Timestamp.from(Instant.now()));
        fragmentRepository.delete(fragment);
        projectRepository.save(project);

        DeleteFragmentResponse response = new DeleteFragmentResponse(fragment.getId(), "Fragment was successfully deleted");
        return ResponseEntity.ok(response);
    }
}
