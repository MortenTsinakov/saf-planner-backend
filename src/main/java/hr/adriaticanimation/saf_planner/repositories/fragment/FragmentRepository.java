package hr.adriaticanimation.saf_planner.repositories.fragment;

import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FragmentRepository extends JpaRepository<Fragment, Integer> {
    List<Fragment> findByProjectOrderByPositionAsc(Project project);
    Optional<Fragment> getFragmentById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Fragment e " +
           "SET e.position = e.position - 1 " +
           "WHERE e.project.id = :projectId AND e.position > :fragmentPosition")
    void shiftFragmentPositionsBackward(@Param("projectId") Long projectId, @Param("fragmentPosition") Integer fragmentPosition);

    @Modifying
    @Transactional
    @Query("UPDATE Fragment e " +
            "SET e.position = e.position + 1 " +
            "WHERE e.project.id = :projectId AND e.position >= :fragmentPosition")
    void shiftFragmentPositionsForward(@Param("projectId") Long projectId, @Param("fragmentPosition") Integer fragmentPosition);

    @Modifying
    @Transactional
    @Query("UPDATE Fragment e " +
           "SET e.position = e.position - 1 " +
           "WHERE e.project.id = :projectId AND e.position > :fromPosition AND e.position <= :toPosition")
    void shiftFragmentPositionsBackward(@Param("projectId") Long projectId,
                                        @Param("fromPosition") Integer fromPosition,
                                        @Param("toPosition") Integer toPosition);

    @Modifying
    @Transactional
    @Query("UPDATE Fragment e " +
           "SET e.position = e.position + 1 " +
           "WHERE e.project.id = :projectId AND e.position >= :fromPosition AND e.position < :toPosition")
    void shiftFragmentPositionsForward(@Param("projectId") Long projectId,
                                       @Param("fromPosition") Integer fromPosition,
                                       @Param("toPosition") Integer toPosition);
}
