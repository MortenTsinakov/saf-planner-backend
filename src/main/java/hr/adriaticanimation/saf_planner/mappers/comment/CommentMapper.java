package hr.adriaticanimation.saf_planner.mappers.comment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.comment.PostCommentRequest;
import hr.adriaticanimation.saf_planner.entities.comment.Comment;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "user")
    @Mapping(target = "fragment", source = "fragment")
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "lastUpdated", expression = "java(Timestamp.from(Instant.now()))")
    Comment postCommentRequestToComment(PostCommentRequest request, User user, Fragment fragment);

    @Mapping(target = "author", expression = "java(String.format(\"%s %s\", comment.getAuthor().getFirstName(), comment.getAuthor().getLastName()))")
    CommentResponse commentToCommentResponse(Comment comment);
}
