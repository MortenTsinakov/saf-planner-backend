package hr.adriaticanimation.saf_planner.services.comment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.DeleteCommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.PostCommentRequest;
import hr.adriaticanimation.saf_planner.dtos.comment.UpdateCommentRequest;
import hr.adriaticanimation.saf_planner.entities.comment.Comment;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.comment.CommentMapper;
import hr.adriaticanimation.saf_planner.repositories.comment.CommentRepository;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.notification.NotificationService;
import hr.adriaticanimation.saf_planner.services.project.SharedProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AuthenticationService authenticationService;
    private final SharedProjectService sharedProjectService;
    private final FragmentRepository fragmentRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

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
        notificationService.createFragmentCommentNotification(user, fragment.getProject().getOwner(), fragment, comment);

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
        comment.setLastUpdated(Timestamp.from(Instant.now()));
        comment = commentRepository.save(comment);

        CommentResponse response = commentMapper.commentToCommentResponse(comment);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete comment with given id.
     * @param commentId - id of the comment to be deleted
     * @return - id of the deleted comment and a sucess message
     */
    public ResponseEntity<DeleteCommentResponse> deleteComment(Long commentId) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Comment comment = commentRepository.getCommentById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with given id not found"));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Comment with given id not found");
        }

        commentRepository.delete(comment);
        DeleteCommentResponse response = new DeleteCommentResponse(
                comment.getId(),
                "Comment was successfully deleted"
        );

        return ResponseEntity.ok(response);
    }
}
