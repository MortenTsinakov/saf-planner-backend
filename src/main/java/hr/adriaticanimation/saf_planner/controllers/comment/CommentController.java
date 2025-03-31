package hr.adriaticanimation.saf_planner.controllers.comment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.DeleteCommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.PostCommentRequest;
import hr.adriaticanimation.saf_planner.dtos.comment.UpdateCommentRequest;
import hr.adriaticanimation.saf_planner.services.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(description = "Post comment for a fragment in a shared project")
    public ResponseEntity<CommentResponse> postComment(@Valid @RequestBody PostCommentRequest request) {
        return commentService.postComment(request);
    }

    @PutMapping
    @Operation(description = "Update comment for a fragment in a shared project")
    public ResponseEntity<CommentResponse> updateComment(@Valid @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(request);
    }

    @DeleteMapping(params = "id")
    @Operation(description = "Delete comment with given id")
    public ResponseEntity<DeleteCommentResponse> deleteComment(@RequestParam("id") Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
