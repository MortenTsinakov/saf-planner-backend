package hr.adriaticanimation.saf_planner.services.screenplay.block_properties;

import hr.adriaticanimation.saf_planner.services.screenplay.ScreenplayConstants;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ParentheticalBlockProperties extends BlockProperties {

    private final PDFont font = new PDType1Font(Standard14Fonts.FontName.COURIER);

    @Override
    public float getMarginLeft() {
        return ScreenplayConstants.MARGIN_LEFT + 1.6f * ScreenplayConstants.INCH;
    }

    @Override
    public float getMarginRight() {
        return ScreenplayConstants.MARGIN_RIGHT + 1.5f * ScreenplayConstants.INCH;
    }

    @Override
    public PDFont getFont() {
        return font;
    }
}
