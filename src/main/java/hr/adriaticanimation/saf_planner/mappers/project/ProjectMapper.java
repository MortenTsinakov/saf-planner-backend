package hr.adriaticanimation.saf_planner.mappers.project;

import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.dtos.project.CreateProjectRequest;
import hr.adriaticanimation.saf_planner.dtos.project.ProjectResponse;
import hr.adriaticanimation.saf_planner.entities.label.Label;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.mappers.label.LabelMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class}, uses = {LabelMapper.class})
public interface ProjectMapper {

    @Mapping(target = "owner", expression = "java(String.format(\"%s %s\", project.getOwner().getFirstName(), project.getOwner().getLastName()))")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "mapLabels")
    ProjectResponse projectToProjectResponse(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "labels", ignore = true)
    Project createProjectRequestToProject(CreateProjectRequest createProjectRequest, User user);

    @Named("mapLabels")
    default List<LabelResponse> mapLabels(List<Label> labels) {
        if (labels == null || labels.isEmpty()) {
            return new ArrayList<>();
        }
        return labels.stream()
                .map(this::toLabelResponse)
                .toList();
    }

    @Named("toLabelResponse")
    LabelResponse toLabelResponse(Label label);
}
