package hr.adriaticanimation.saf_planner.services.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.DeleteFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.UpdateFragmentRequest;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.fragment.FragmentMapper;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.repositories.project.ProjectRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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
    public ResponseEntity<FragmentResponse> updateFragment(Long fragmentId, UpdateFragmentRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Fragment fragment = fragmentRepository.getFragmentById(fragmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Fragment not found"));
        Project project = fragment.getProject();

        if (!project.getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Fragment not found");
        }

        request.shortDescription().ifPresent(fragment::setShortDescription);
        request.longDescription().ifPresent(fragment::setLongDescription);
        request.durationInSeconds().ifPresent(fragment::setDurationInSeconds);
        request.onTimeline().ifPresent(fragment::setOnTimeline);
        request.position().ifPresent(position -> moveFragment(project, fragment, position));

        project.setUpdatedAt(Timestamp.from(Instant.now()));

        fragmentRepository.save(fragment);
        projectRepository.save(project);

        FragmentResponse response = fragmentMapper.fragmentToFragmentResponse(fragment);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<DeleteFragmentResponse> deleteFragment(Long fragmentId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Fragment fragment = fragmentRepository.getFragmentById(fragmentId)
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

    @Transactional
    public void moveFragment(Project project, Fragment fragment, Integer position) {
        if (fragment.getPosition() == position) {
            throw new IllegalArgumentException("Fragment is already in requested position");
        }

        if (position > fragment.getPosition()) {
            fragmentRepository.shiftFragmentPositionsBackward(project.getId(), fragment.getPosition(), position);
        } else {
            fragmentRepository.shiftFragmentPositionsForward(project.getId(), position, fragment.getPosition());
        }

        fragment.setPosition(position);
    }
}
