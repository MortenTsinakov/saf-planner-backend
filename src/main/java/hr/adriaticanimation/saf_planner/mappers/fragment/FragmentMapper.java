package hr.adriaticanimation.saf_planner.mappers.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.image.FragmentImageResponse;
import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.image.FragmentImage;
import hr.adriaticanimation.saf_planner.entities.label.Label;
import hr.adriaticanimation.saf_planner.entities.label.LabelInFragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.mappers.label.LabelMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = LabelMapper.class)
public interface FragmentMapper {

    @Mapping(target = "projectId", expression = "java(fragment.getProject().getId())")
    @Mapping(target = "labels", source = "labelInFragmentList", qualifiedByName = "mapLabels")
    @Mapping(target = "images", source = "images", qualifiedByName = "mapImages")
    FragmentResponse fragmentToFragmentResponse(Fragment fragment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "projectForFragment")
    Fragment createFragmentRequestToFragment(CreateFragmentRequest request, Project projectForFragment);

    @Named("mapLabels")
    default List<LabelResponse> mapLabels(List<LabelInFragment> labelsInFragment) {
        if (labelsInFragment == null || labelsInFragment.isEmpty()) {
            return new ArrayList<>();
        }
        return labelsInFragment.stream()
                .map(labelInFragment -> toLabelResponse(labelInFragment.getLabel()))
                .toList();
    }

    @Named("mapImages")
    default List<FragmentImageResponse> mapImages(List<FragmentImage> fragmentImages) {
        if (fragmentImages == null || fragmentImages.isEmpty()) {
            return new ArrayList<>();
        }
        return fragmentImages.stream()
                .map(image -> new FragmentImageResponse(
                        image.getFragment().getId(),
                        String.format("%s.%s", image.getId(), image.getFileExtension()),
                        image.getDescription()
                ))
                .toList();
    }

    @Named("toLabelResponse")
    LabelResponse toLabelResponse(Label label);
}
