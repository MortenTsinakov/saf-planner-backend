package hr.adriaticanimation.saf_planner.services.screenplay;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ScreenplayConstants {

    // Points to inch
    public static final float INCH = 72f;

    // Fonts
    public static final PDType1Font FONT = new PDType1Font(Standard14Fonts.FontName.COURIER);
    public static final float FONT_SIZE = 12f;

    // Line
    public static final float LINE_HEIGHT = FONT_SIZE * 1.5f;

    // Page margins
    public static final float MARGIN_TOP = INCH;
    public static final float MARGIN_BOTTOM = INCH;
    public static final float MARGIN_LEFT = INCH * 1.5f;
    public static final float MARGIN_RIGHT = INCH;

    // Page
    public static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    public static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
}
