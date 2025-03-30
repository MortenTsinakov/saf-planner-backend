package hr.adriaticanimation.saf_planner.services.comment;

import hr.adriaticanimation.saf_planner.repositories.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
}
