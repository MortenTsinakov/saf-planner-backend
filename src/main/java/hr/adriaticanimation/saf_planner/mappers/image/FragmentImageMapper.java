package hr.adriaticanimation.saf_planner.mappers.image;

import hr.adriaticanimation.saf_planner.dtos.image.FragmentImageResponse;
import hr.adriaticanimation.saf_planner.entities.image.FragmentImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FragmentImageMapper {

    @Mapping(target = "fragmentId", expression = "java(fragmentImage.getFragment().getId())")
    @Mapping(target = "image", expression = "java(String.format(\"%s.%s\", fragmentImage.getId(), fragmentImage.getFileExtension()))")
    FragmentImageResponse mapFragmentImageResponse(FragmentImage fragmentImage);
}
