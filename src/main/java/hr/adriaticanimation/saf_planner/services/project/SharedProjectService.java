package hr.adriaticanimation.saf_planner.services.project;

import hr.adriaticanimation.saf_planner.dtos.fragment.SharedProjectFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.project.ShareProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.SharedProjectResponse;
import hr.adriaticanimation.saf_planner.dtos.project.StopSharingProjectResponse;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.project.SharedProject;
import hr.adriaticanimation.saf_planner.entities.project.SharedProjectId;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.fragment.FragmentMapper;
import hr.adriaticanimation.saf_planner.mappers.project.SharedProjectMapper;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.repositories.project.ProjectRepository;
import hr.adriaticanimation.saf_planner.repositories.project.SharedProjectRepository;
import hr.adriaticanimation.saf_planner.repositories.user.UserRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedProjectService {

    private final SharedProjectRepository sharedProjectRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final FragmentRepository fragmentRepository;
    private final SharedProjectMapper sharedProjectMapper;
    private final FragmentMapper fragmentMapper;


    public ResponseEntity<SharedProjectResponse> getSharedProjectById(Long id) {
        Project project = fetchProject(id);
        SharedProjectResponse response = sharedProjectMapper.projectToSharedProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<SharedProjectFragmentResponse>> getSharedProjectFragments(Long projectId) {
        Project project = fetchProject(projectId);
        List<Fragment> fragments = fragmentRepository.findByProjectOrderByPositionAsc(project);
        List<SharedProjectFragmentResponse> response = fragments
                .stream()
                .map(fragmentMapper::fragmentToSharedProjectFragmentResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    public Project fetchProject(Long id) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        SharedProjectId sharedProjectId = new SharedProjectId(id, user.getId());
        SharedProject sharedProject = sharedProjectRepository.findById(sharedProjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found"));
        return sharedProject.getProject();
    }

    public ResponseEntity<SharedProjectResponse> shareProject(ShareProjectRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Project project = projectRepository.getProjectByIdAndOwner(request.projectId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found"));
        User sharedWith = userRepository.findById(request.shareWithId())
                .orElseThrow(() -> new ResourceNotFoundException("User with given id was not found"));

        SharedProject sharedProject = new SharedProject(
                project,
                sharedWith
        );
        sharedProject = sharedProjectRepository.save(sharedProject);
        SharedProjectResponse sharedProjectResponse = sharedProjectMapper.projectToSharedProjectResponse(sharedProject.getProject());
        return ResponseEntity.ok(sharedProjectResponse);
    }

    public ResponseEntity<List<SharedProjectResponse>> getSharedProjects() {
        User user = authenticationService.getUserFromSecurityContextHolder();
        List<SharedProject> sharedProjects = sharedProjectRepository.findAllBySharedWith(user);
        List<SharedProjectResponse> response = sharedProjects
                .stream()
                .map(sp -> sharedProjectMapper.projectToSharedProjectResponse(sp.getProject()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<StopSharingProjectResponse> stopSharingProject(Long projectId, Long userId) {
        User user = authenticationService.getUserFromSecurityContextHolder();

        // Verify that the project is owned by the user making the request
        projectRepository.getProjectByIdAndOwner(projectId, user)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        sharedProjectRepository.deleteByProjectIdAndSharedWithId(projectId, userId);
        StopSharingProjectResponse response = new StopSharingProjectResponse(
                projectId,
                userId,
                "Successfully stopped sharing the project"
        );

        return ResponseEntity.ok(response);
    }
}
