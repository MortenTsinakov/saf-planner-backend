package hr.adriaticanimation.saf_planner.repositories.comment;

import hr.adriaticanimation.saf_planner.entities.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
