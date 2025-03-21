package hr.adriaticanimation.saf_planner.repositories.project;

import hr.adriaticanimation.saf_planner.entities.project.SharedProject;
import hr.adriaticanimation.saf_planner.entities.project.SharedProjectId;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SharedProjectRepository extends JpaRepository<SharedProject, SharedProjectId> {
    List<SharedProject> findAllBySharedWith(User sharedWith);
    @Modifying
    @Query("DELETE FROM SharedProject sp " +
           "WHERE sp.project.id = :projectId " +
           "AND sp.sharedWith.id = :userId")
    void deleteByProjectIdAndSharedWithId(@Param("projectId") Long projectId, @Param("userId") Long sharedWithId);
}
