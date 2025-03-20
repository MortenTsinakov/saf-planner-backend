package hr.adriaticanimation.saf_planner.entities.project;

import hr.adriaticanimation.saf_planner.entities.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "shared_projects")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedProject {

    public SharedProject(Project project, User sharedWith) {
        this.id = new SharedProjectId(project.getId(), sharedWith.getId());
        this.project = project;
        this.sharedWith = sharedWith;
    }

    @EmbeddedId
    private SharedProjectId id;
    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project", referencedColumnName = "id")
    private Project project;
    @ManyToOne
    @MapsId("sharedWithId")
    @JoinColumn(name = "shared_with", referencedColumnName = "id")
    private User sharedWith;
    @Column(name = "shared_at")
    private Timestamp sharedAt;
}
