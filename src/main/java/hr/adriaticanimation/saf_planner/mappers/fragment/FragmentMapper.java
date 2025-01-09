package hr.adriaticanimation.saf_planner.mappers.fragment;

import hr.adriaticanimation.saf_planner.dtos.fragment.CreateFragmentRequest;
import hr.adriaticanimation.saf_planner.dtos.fragment.FragmentResponse;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FragmentMapper {

    @Mapping(target = "projectId", expression = "java(fragment.getProject().getId())")
    FragmentResponse fragmentToFragmentResponse(Fragment fragment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "projectForFragment")
    Fragment createFragmentRequestToFragment(CreateFragmentRequest request, Project projectForFragment);
}
