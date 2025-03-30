package hr.adriaticanimation.saf_planner.services.comment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.PostCommentRequest;
import hr.adriaticanimation.saf_planner.dtos.comment.UpdateCommentRequest;
import hr.adriaticanimation.saf_planner.entities.comment.Comment;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.comment.CommentMapper;
import hr.adriaticanimation.saf_planner.repositories.comment.CommentRepository;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.project.SharedProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AuthenticationService authenticationService;
    private final SharedProjectService sharedProjectService;
    private final FragmentRepository fragmentRepository;
    private final CommentMapper commentMapper;

    /**
     * Post comment for a fragment in a shared project.
     * @param request - request containing the fragment id and comment content
     * @return - comment response
     */
    public ResponseEntity<CommentResponse> postComment(PostCommentRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Fragment fragment = fragmentRepository.getFragmentById(request.fragmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fragment with given id not found"));
        // Basically checks whether the project is shared with the user. User can't comment a fragment
        // in a project that is not shared with them.
        sharedProjectService.fetchProject(fragment.getProject().getId());

        Comment comment = commentMapper.postCommentRequestToComment(request, user, fragment);
        comment = commentRepository.save(comment);

        CommentResponse response = commentMapper.commentToCommentResponse(comment);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a comment written by the user
     * @param request - request for updating a comment
     * @return - updated comment
     */
    public ResponseEntity<CommentResponse> updateComment(UpdateCommentRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Comment comment = commentRepository.getCommentById(request.commentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment with given id not found"));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Comment with given id not found");
        }

        comment.setContent(request.content());
        comment = commentRepository.save(comment);

        CommentResponse response = commentMapper.commentToCommentResponse(comment);
        return ResponseEntity.ok(response);
    }
}
