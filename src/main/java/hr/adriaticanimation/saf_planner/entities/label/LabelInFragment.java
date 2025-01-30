package hr.adriaticanimation.saf_planner.entities.label;

import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "labels_in_fragments")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelInFragment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    @JoinColumn(name = "label", referencedColumnName = "id")
    private Label label;
    @OneToOne
    @JoinColumn(name = "fragment", referencedColumnName = "id")
    private Fragment fragment;
}
