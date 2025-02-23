package hr.adriaticanimation.saf_planner.entities.image;

import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fragment_images")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(FragmentImageEntityListener.class)
public class FragmentImage {

    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "file_extension", nullable = false)
    private String fileExtension;
    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "fragment", referencedColumnName = "id")
    private Fragment fragment;
}
