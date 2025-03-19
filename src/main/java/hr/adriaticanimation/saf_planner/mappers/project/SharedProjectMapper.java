package hr.adriaticanimation.saf_planner.mappers.project;

import hr.adriaticanimation.saf_planner.dtos.project.SharedProjectResponse;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SharedProjectMapper {

    @Mapping(target = "owner", expression = "java(String.format(\"%s %s\", project.getOwner().getFirstName(), project.getOwner().getLastName()))")
    SharedProjectResponse projectToSharedProjectResponse(Project project);
}
