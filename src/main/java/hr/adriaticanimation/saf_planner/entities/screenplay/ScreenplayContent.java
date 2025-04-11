package hr.adriaticanimation.saf_planner.entities.screenplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenplayContent {
    private Long id;
    private String type;
    private List<ScreenplayBlock> children;
}
