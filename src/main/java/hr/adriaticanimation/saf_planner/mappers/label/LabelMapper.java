package hr.adriaticanimation.saf_planner.mappers.label;

import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.entities.label.Label;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelResponse labelToLabelResponse(Label label);
    List<LabelResponse> labelsToLabelResponses(List<Label> labels);
}
