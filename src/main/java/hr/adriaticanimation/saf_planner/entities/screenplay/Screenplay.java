package hr.adriaticanimation.saf_planner.entities.screenplay;

import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.utils.converters.ScreenplayContentConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.sql.Timestamp;

@Entity
@Table(name = "screenplays")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screenplay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project", referencedColumnName = "id", nullable = false)
    private Project project;
    @Convert(converter = ScreenplayContentConverter.class)
    @ColumnTransformer(write = "?::json")
    @Column(name = "content", columnDefinition = "json")
    private ScreenplayContent content;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
