package hr.adriaticanimation.saf_planner.mappers.project;

import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class})
public interface ProjectMapper {

    @Mapping(target = "owner", expression = "java(String.format(\"%s %s\", project.getOwner().getFirstName(), project.getOwner().getLastName()))")
    ProjectResponse projectToProjectResponse(Project project);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "owner", source = "user")
    Project createProjectRequestToProject(CreateProjectRequest createProjectRequest, User user);
}
