package hr.adriaticanimation.saf_planner.services.screenplay.block_properties;

import hr.adriaticanimation.saf_planner.services.screenplay.ScreenplayConstants;
import org.apache.pdfbox.pdmodel.font.PDFont;

public abstract class BlockProperties {

    public abstract float getMarginLeft();
    public abstract float getMarginRight();
    public float getBlockWidth() {
        return ScreenplayConstants.PAGE_WIDTH - getMarginLeft() - getMarginRight();
    };
    public abstract PDFont getFont();
    public float getFontSize() {
        return 12f;
    };

}
