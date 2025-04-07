package hr.adriaticanimation.saf_planner.services.screenplay;

import hr.adriaticanimation.saf_planner.dtos.screenplay.CreateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.DeleteScreenplayResponse;
import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayResponse;
import hr.adriaticanimation.saf_planner.dtos.screenplay.UpdateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.UpdateScreenplayResponse;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.screenplay.Screenplay;
import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayBlock;
import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayContent;
import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayElement;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.screenplay.ScreenplayMapper;
import hr.adriaticanimation.saf_planner.repositories.screenplay.ScreenplayRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenplayService {

    private final ScreenplayRepository screenplayRepository;
    private final ScreenplayMapper screenplayMapper;
    private final ProjectService projectService;
    private final AuthenticationService authenticationService;

    /**
     * Get the screenplay for a project. If no screenplay has been created yet,
     * return a template for a screenplay that the user can develop further.
     *
     * @param id - project id
     * @return - screenplay for the project
     */
    public ResponseEntity<ScreenplayResponse> getScreenplayForProject(Long id) {
        // The check whether the project belongs to the user is done in
        // ProjectService class.
        Project project = projectService.getUserProjectById(id);
        Screenplay screenplay = screenplayRepository.getScreenplayByProject(project)
                .orElse(getEmptyScreenplay(project));
        ScreenplayResponse response = screenplayMapper.screenplayToScreenplayResponse(screenplay);
        return ResponseEntity.ok(response);
    }

    /**
     * Create an empty screenplay template. It can be used as a starting
     * point for a user who haven't saved any screenplays before.
     *
     * @param project - project where the (potential) screenplay belongs.
     * @return - template for screenplay (Screenplay object)
     */
    private Screenplay getEmptyScreenplay(Project project) {
        ScreenplayElement el = new ScreenplayElement("fade in:");
        ScreenplayBlock bl = ScreenplayBlock.builder()
                .type("header")
                .children(List.of(el))
                .build();
        ScreenplayContent content = ScreenplayContent.builder()
                .id(1L)
                .type("screenplay")
                .children(List.of(bl))
                .build();

        return Screenplay.builder()
                .content(content)
                .project(project)
                .build();
    }

    /**
     * Save new screenplay to the database.
     *
     * @param request - request containing the project id and content of the screenplay
     * @return - created screenplay
     */
    public ResponseEntity<ScreenplayResponse> createScreenplay(CreateScreenplayRequest request) {
        Project project = projectService.getUserProjectById(request.projectId());
        Screenplay screenplay = screenplayMapper.createScreenplayRequestToScreenplay(request, project);
        screenplayRepository.save(screenplay);
        ScreenplayResponse response = screenplayMapper.screenplayToScreenplayResponse(screenplay);
        return ResponseEntity.ok(response);
    }

    /**
     * Update screenplay with given id. Overwrite the current content with the content
     * provided.
     *
     * @param request - request containing the screenplay id and new content
     * @return - message indicating a successful update (NB! The screenplay itself is not provided with the response)
     */
    public ResponseEntity<UpdateScreenplayResponse> updateScreenplay(UpdateScreenplayRequest request) {
        Screenplay screenplay = screenplayRepository.getScreenplayById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Screenplay with given id not found"));
        User user = authenticationService.getUserFromSecurityContextHolder();
        if (!screenplay.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Screenplay with given id not found");
        }

        screenplay.setContent(request.content());
        screenplay.setUpdatedAt(Timestamp.from(Instant.now()));
        screenplayRepository.save(screenplay);

        UpdateScreenplayResponse response = new UpdateScreenplayResponse(screenplay.getId(), "Screenplay was successfully updated");

        return ResponseEntity.ok(response);
    }

    /**
     * Delete screenplay with given id.
     *
     * @param id - id of the screenplay to be deleted
     * @return - message indicating a successful delete operation
     */
    public ResponseEntity<DeleteScreenplayResponse> deleteScreenplay(Long id) {
        Screenplay screenplay = screenplayRepository.getScreenplayById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screenplay with given id was not found"));
        User user = authenticationService.getUserFromSecurityContextHolder();
        if (!screenplay.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Screenplay with given id was not found");
        }

        screenplayRepository.delete(screenplay);

        DeleteScreenplayResponse response = new DeleteScreenplayResponse(id, "Screenplay was successfully deleted");
        return ResponseEntity.ok(response);
    }
}
