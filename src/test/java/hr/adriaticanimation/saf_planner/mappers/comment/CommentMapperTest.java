package hr.adriaticanimation.saf_planner.mappers.comment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.PostCommentRequest;
import hr.adriaticanimation.saf_planner.entities.comment.Comment;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void testPostCommentRequestToComment() {
        PostCommentRequest request = new PostCommentRequest(
                1L,
                "Comment content"
        );
        Fragment fragment = Fragment.builder()
                .id(1L)
                .build();
        User user = User.builder()
                .id(1L)
                .build();

        Comment comment = commentMapper.postCommentRequestToComment(request, user, fragment);

        assertNotNull(comment);
        assertNull(comment.getId());
        assertEquals(request.content(), comment.getContent());
        assertEquals(user.getId(), comment.getAuthor().getId());
        assertEquals(fragment.getId(), comment.getFragment().getId());
        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getLastUpdated());
    }

    @Test
    void testCommentToCommentResponse() {
        String content = "Comment content";
        User user = User.builder()
                .id(3L)
                .firstName("Aadam")
                .lastName("Aforementioned")
                .build();
        Fragment fragment = Fragment.builder()
                .id(5L)
                .build();
        Timestamp timestamp = Timestamp.from(Instant.now());
        Comment comment = Comment.builder()
                .id(1L)
                .content(content)
                .author(user)
                .fragment(fragment)
                .createdAt(timestamp)
                .lastUpdated(timestamp)
                .build();

        CommentResponse response = commentMapper.commentToCommentResponse(comment);

        assertNotNull(response);
        assertEquals(1L, comment.getId());
        assertEquals(content, response.content());
        assertEquals(String.format("%s %s", user.getFirstName(), user.getLastName()), response.author());
        assertEquals(timestamp, response.lastUpdated());
    }

}