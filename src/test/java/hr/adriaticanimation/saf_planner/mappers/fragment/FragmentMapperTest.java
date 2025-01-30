package hr.adriaticanimation.saf_planner.mappers.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.label.Label;
import hr.adriaticanimation.saf_planner.entities.label.LabelInFragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

        Label label1 = Label.builder()
                .id(1L)
                .project(project)
                .color("#000111")
                .description("Description")
                .build();
        Label label2 = Label.builder()
                .id(2L)
                .project(project)
                .color("#111222")
                .description(null)
                .build();
        LabelInFragment labelInFragment1 = new LabelInFragment();
        labelInFragment1.setFragment(fragment);
        labelInFragment1.setLabel(label1);
        LabelInFragment labelInFragment2 = new LabelInFragment();
        labelInFragment2.setFragment(fragment);
        labelInFragment2.setLabel(label2);

        List<LabelInFragment> labelInFragmentList = List.of(labelInFragment1, labelInFragment2);

        fragment.setLabelInFragmentList(labelInFragmentList);

        FragmentResponse result = fragmentMapper.fragmentToFragmentResponse(fragment);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(durationInSeconds, result.durationInSeconds());
        assertEquals(onTimeline, result.onTimeline());
        assertEquals(shortDescription, result.shortDescription());
        assertEquals(longDescription, result.longDescription());
        assertEquals(position, result.position());
        assertEquals(project.getId(), result.projectId());
        assertEquals(labelInFragmentList.size(), result.labels().size());
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