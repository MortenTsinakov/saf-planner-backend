package hr.adriaticanimation.saf_planner.entities.label;

import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "labels_in_fragments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelInFragment {

    public LabelInFragment(Label label, Fragment fragment) {
        this.id = new LabelInFragmentId(label.getId(), fragment.getId());
        this.label = label;
        this.fragment = fragment;
    }

    @EmbeddedId
    @Column(name = "id", nullable = false)
    private LabelInFragmentId id;
    @ManyToOne
    @MapsId("labelId")
    @JoinColumn(name = "label", referencedColumnName = "id")
    private Label label;
    @ManyToOne
    @MapsId("fragmentId")
    @JoinColumn(name = "fragment", referencedColumnName = "id")
    private Fragment fragment;
}
