package hr.adriaticanimation.saf_planner.mappers.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class FragmentMapperTest {

    @Spy
    private FragmentMapper fragmentMapper = Mappers.getMapper(FragmentMapper.class);

    @Test
    void testFragmentToFragmentResponse() {
        Project project = Project.builder()
                .id(1L)
                .build();
        Long id = 1L;
        int durationInSeconds = 15;
        boolean onTimeline = false;
        String shortDescription = "Short";
        String longDescription = "Long";
        int position = 2;
        Fragment fragment = Fragment.builder()
                .id(id)
                .durationInSeconds(durationInSeconds)
                .onTimeline(onTimeline)
                .shortDescription(shortDescription)
                .longDescription(longDescription)
                .position(position)
                .project(project)
                .build();

        FragmentResponse result = fragmentMapper.fragmentToFragmentResponse(fragment);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(durationInSeconds, result.durationInSeconds());
        assertEquals(onTimeline, result.onTimeline());
        assertEquals(shortDescription, result.shortDescription());
        assertEquals(longDescription, result.longDescription());
        assertEquals(position, result.position());
        assertEquals(project.getId(), result.projectId());
    }

    @Test
    void testCreateFragmentRequestToFragment() {
        Project project = Project.builder()
                .id(13L)
                .build();
        CreateFragmentRequest request = new CreateFragmentRequest(
                "short",
                "long",
                15,
                true,
                10,
                project.getId()
        );

        Fragment fragment = fragmentMapper.createFragmentRequestToFragment(request, project);

        assertNotNull(fragment);
        assertNull(fragment.getId());
        assertEquals(request.shortDescription(), fragment.getShortDescription());
        assertEquals(request.longDescription(), fragment.getLongDescription());
        assertEquals(request.durationInSeconds(), fragment.getDurationInSeconds());
        assertEquals(request.onTimeline(), fragment.isOnTimeline());
        assertEquals(request.position(), fragment.getPosition());
        assertEquals(project, fragment.getProject());
    }
}