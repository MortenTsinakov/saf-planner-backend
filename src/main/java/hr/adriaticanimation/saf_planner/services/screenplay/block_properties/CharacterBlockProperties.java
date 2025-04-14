package hr.adriaticanimation.saf_planner.services.screenplay.block_properties;

import hr.adriaticanimation.saf_planner.services.screenplay.ScreenplayConstants;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class CharacterBlockProperties extends BlockProperties {

    private final PDFont font = new PDType1Font(Standard14Fonts.FontName.COURIER);

    @Override
    public float getMarginLeft() {
        return ScreenplayConstants.MARGIN_LEFT + 2.2f * ScreenplayConstants.INCH;
    }

    @Override
    public float getMarginRight() {
        return ScreenplayConstants.MARGIN_RIGHT;
    }

    @Override
    public PDFont getFont() {
        return font;
    }
}
