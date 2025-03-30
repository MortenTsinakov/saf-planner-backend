package hr.adriaticanimation.saf_planner.mappers.fragment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.fragment.SharedProjectFragmentResponse;
import hr.adriaticanimation.saf_planner.dtos.image.FragmentImageResponse;
import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.entities.comment.Comment;
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
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = LabelMapper.class)
public interface FragmentMapper {

    @Mapping(target = "projectId", expression = "java(fragment.getProject().getId())")
    @Mapping(target = "labels", source = "labelInFragmentList", qualifiedByName = "mapLabels")
    @Mapping(target = "images", source = "images", qualifiedByName = "mapImages")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "mapComments")
    FragmentResponse fragmentToFragmentResponse(Fragment fragment);

    @Mapping(target = "projectId", expression = "java(fragment.getProject().getId())")
    @Mapping(target = "images", source = "images", qualifiedByName = "mapImages")
    SharedProjectFragmentResponse fragmentToSharedProjectFragmentResponse(Fragment fragment);

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

    @Named("mapComments")
    default List<CommentResponse> mapComments(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        return comments
                .stream()
                .sorted(Comparator.comparing(Comment::getLastUpdated).reversed())
                .map(comment -> new CommentResponse(
                     comment.getId(),
                     comment.getContent(),
                     String.format("%s %s", comment.getAuthor().getFirstName(), comment.getAuthor().getLastName()),
                     comment.getLastUpdated()
                ))
                .toList();
    }

    @Named("toLabelResponse")
    LabelResponse toLabelResponse(Label label);
}
