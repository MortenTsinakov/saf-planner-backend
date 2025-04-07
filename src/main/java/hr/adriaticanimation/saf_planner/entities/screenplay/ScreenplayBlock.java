package hr.adriaticanimation.saf_planner.entities.screenplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScreenplayBlock {
    private String type;
    private List<ScreenplayElement> children;
}
