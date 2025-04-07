package hr.adriaticanimation.saf_planner.mappers.screenplay;

import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayResponse;
import hr.adriaticanimation.saf_planner.entities.screenplay.Screenplay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScreenplayMapper {
    @Mapping(target = "projectId", expression = "java(screenplay.getProject().getId())")
    ScreenplayResponse screenplayToScreenplayResponse(Screenplay screenplay);
}
