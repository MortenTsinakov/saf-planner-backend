package hr.adriaticanimation.saf_planner.mappers.project;

import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    void testProjectToProjectResponse() {
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
        Timestamp createdAt = Timestamp.valueOf("2024-12-30 13:30:00");
        Timestamp updatedAt = Timestamp.valueOf("2024-12-31 13:30:00");
        Project project = Project.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .estimatedLengthInSeconds(60)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .owner(user)
                .build();

        ProjectResponse result = projectMapper.projectToProjectResponse(project);

        assertNotNull(result);
        assertEquals(project.getId(), result.id());
        assertEquals(project.getTitle(), result.title());
        assertEquals(project.getDescription(), result.description());
        assertEquals(project.getEstimatedLengthInSeconds(), result.estimatedLengthInSeconds());
        assertEquals(project.getCreatedAt(), result.createdAt());
        assertEquals(project.getUpdatedAt(), result.updatedAt());
        assertEquals(String.format("%s %s", user.getFirstName(), user.getLastName()), result.owner());
    }

    @Test
    void testCreateProjectRequestToProject() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Title",
                "Description",
                360
        );
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        Project result = projectMapper.createProjectRequestToProject(request, user);

        assertNotNull(result);
        assertNull(result.getId());
        assertInstanceOf(Timestamp.class, result.getCreatedAt());
        assertInstanceOf(Timestamp.class, result.getUpdatedAt());
        assertEquals(user, result.getOwner());
    }
}