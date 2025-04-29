package hr.adriaticanimation.saf_planner.services.comment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.DeleteCommentResponse;
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
import hr.adriaticanimation.saf_planner.services.notification.NotificationService;
import hr.adriaticanimation.saf_planner.services.project.SharedProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private SharedProjectService sharedProjectService;
    @Mock
    private FragmentRepository fragmentRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private CommentService commentService;

    @Test
    void testPostCommentSuccess() {
        PostCommentRequest request = new PostCommentRequest(1L, "Testing");
        User user = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Project project = Project.builder().id(1L).owner(owner).build();
        Fragment fragment = Fragment.builder().id(1L).project(project).build();
        Comment comment = Comment.builder()
                .id(1L)
                .content(request.content())
                .build();
        CommentResponse commentResponse = new CommentResponse(1L, request.content(), null, null, null);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.of(fragment));
        when(sharedProjectService.fetchProject(fragment.getProject().getId())).thenReturn(project);
        when(commentMapper.postCommentRequestToComment(request, user, fragment)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCommentResponse(comment)).thenReturn(commentResponse);

        ResponseEntity<CommentResponse> result = commentService.postComment(request);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verify(sharedProjectService).fetchProject(fragment.getProject().getId());
        verify(commentMapper).postCommentRequestToComment(request, user, fragment);
        verify(commentRepository).save(comment);
        verify(commentMapper).commentToCommentResponse(comment);
        verify(notificationService).createFragmentCommentNotification(user, owner, fragment, comment);
    }

    @Test
    void testPostCommentFragmentNotFound() {
        PostCommentRequest request = new PostCommentRequest(1L, "Testing");
        User user = User.builder().id(1L).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(fragmentRepository.getFragmentById(request.fragmentId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.postComment(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(fragmentRepository).getFragmentById(request.fragmentId());
        verifyNoInteractions(sharedProjectService);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void testUpdateComment() {
        UpdateCommentRequest request = new UpdateCommentRequest(1L, "Testing");
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder()
                .id(1L)
                .author(user)
                .build();
        CommentResponse response = new CommentResponse(1L, request.content(), null, null, null);

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(commentRepository.getCommentById(request.commentId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCommentResponse(comment)).thenReturn(response);

        ResponseEntity<CommentResponse> result = commentService.updateComment(request);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(comment.getContent(), request.content());
        assertNotNull(comment.getLastUpdated());

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(commentRepository).getCommentById(request.commentId());
        verify(commentRepository).save(comment);
        verify(commentMapper).commentToCommentResponse(comment);
    }

    @Test
    void testUpdateCommentCommentNotFound() {
        UpdateCommentRequest request = new UpdateCommentRequest(1L, "Testing");
        User user = User.builder().id(1L).build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(commentRepository.getCommentById(request.commentId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(commentRepository).getCommentById(request.commentId());
        verifyNoMoreInteractions(commentRepository);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void testUpdateCommentUserIsNotAuthor() {
        UpdateCommentRequest request = new UpdateCommentRequest(1L, "Testing");
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Comment comment = Comment.builder()
                .id(request.commentId())
                .author(user2)
                .build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(commentRepository.getCommentById(request.commentId())).thenReturn(Optional.of(comment));

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(request));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(commentRepository).getCommentById(request.commentId());
        verifyNoMoreInteractions(commentRepository);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void testDeleteComment() {
        Long commentId = 1L;
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder()
                .id(commentId)
                .author(user)
                .build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(commentRepository.getCommentById(commentId)).thenReturn(Optional.of(comment));

        ResponseEntity<DeleteCommentResponse> result = commentService.deleteComment(commentId);

        assertTrue(result.getStatusCode().is2xxSuccessful());

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(commentRepository).getCommentById(commentId);
        verify(commentRepository).delete(comment);
    }

    @Test
    void testDeleteCommentCommentNotFound() {
        Long commentId = 1L;
        User user = User.builder().build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(commentRepository.getCommentById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(commentId));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(commentRepository).getCommentById(commentId);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void testDeleteCommentUserIsNotAuthor() {
        Long commentId = 1L;
        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Comment comment = Comment.builder()
                .id(commentId)
                .author(user2)
                .build();

        when(authenticationService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(commentRepository.getCommentById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(commentId));

        verify(authenticationService).getUserFromSecurityContextHolder();
        verify(commentRepository).getCommentById(commentId);
        verifyNoMoreInteractions(commentRepository);

    }
}