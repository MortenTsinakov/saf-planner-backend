package hr.adriaticanimation.saf_planner.entities.fragment;

import hr.adriaticanimation.saf_planner.entities.label.LabelInFragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "fragments")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fragment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "long_description")
    private String longDescription;
    @Column(name = "duration_in_seconds")
    private int durationInSeconds;
    @Column(name = "on_timeline", nullable = false)
    private boolean onTimeline;
    @Column(name = "position")
    private int position;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project", referencedColumnName = "id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "fragment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabelInFragment> labelInFragmentList;
}
