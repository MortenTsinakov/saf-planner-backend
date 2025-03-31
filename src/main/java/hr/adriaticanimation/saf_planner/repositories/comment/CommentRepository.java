package hr.adriaticanimation.saf_planner.repositories.comment;

import hr.adriaticanimation.saf_planner.entities.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> getCommentById(Long id);
}
