package hr.adriaticanimation.saf_planner.mappers.screenplay;

import hr.adriaticanimation.saf_planner.dtos.screenplay.CreateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayResponse;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.screenplay.Screenplay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class})
public interface ScreenplayMapper {
    @Mapping(target = "projectId", expression = "java(screenplay.getProject().getId())")
    ScreenplayResponse screenplayToScreenplayResponse(Screenplay screenplay);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "project")
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    Screenplay createScreenplayRequestToScreenplay(CreateScreenplayRequest request, Project project);
}
