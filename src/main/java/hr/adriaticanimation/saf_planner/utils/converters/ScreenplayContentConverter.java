package hr.adriaticanimation.saf_planner.utils.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayContent;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ScreenplayContentConverter implements AttributeConverter<ScreenplayContent, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ScreenplayContent screenplayContent) {
        try {
            return objectMapper.writeValueAsString(screenplayContent);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not convert ScreenplayContent to JSON", e);
        }
    }

    @Override
    public ScreenplayContent convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, ScreenplayContent.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not convert ScreenplayContent to JSON", e);
        }
    }
}
