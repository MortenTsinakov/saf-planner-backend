package hr.adriaticanimation.saf_planner.services.screenplay;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ScreenplayConstants {

    // Points to inch
    public static final float INCH = 72f;

    // Fonts
    public static final PDType1Font FONT_HEADING = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
    public static final PDType1Font FONT = new PDType1Font(Standard14Fonts.FontName.COURIER);
    public static final float FONT_SIZE = 12f;

    // Line
    public static final float LINE_HEIGHT = FONT_SIZE * 1.5f;

    // Page margins
    public static final float MARGIN_TOP = INCH;
    public static final float MARGIN_BOTTOM = INCH;
    public static final float MARGIN_LEFT = INCH * 1.5f;
    public static final float MARGIN_RIGHT = INCH;

    // Block margins
    public static final float HEADER_LEFT_MARGIN = 0f;
    public static final float HEADER_RIGHT_MARGIN = 0f;
    public static final float ACTION_LEFT_MARGIN = 0f;
    public static final float ACTION_RIGHT_MARGIN = 0f;
    public static final float CHARACTER_LEFT_MARGIN = 2.2f * INCH;
    public static final float CHARACTER_RIGHT_MARGIN = 0f;
    public static final float PARENTHETICAL_LEFT_MARGIN = 1.6f * INCH;
    public static final float PARENTHETICAL_RIGHT_MARGIN = 1.5f * INCH;
    public static final float DIALOGUE_LEFT_MARGIN = INCH;
    public static final float DIALOGUE_RIGHT_MARGIN = 1.5f * INCH;
    public static final float TRANSITION_LEFT_MARGIN = 4.5f * INCH;
    public static final float TRANSITION_RIGHT_MARGIN = 0f;

    // Page
    public static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    public static final float PAGE_WIDTH = PDRectangle.A4.getWidth();

    // Block widths
    public static final float HEADING_BLOCK_WIDTH = PAGE_WIDTH - HEADER_LEFT_MARGIN - HEADER_RIGHT_MARGIN;
    public static final float ACTION_BLOCK_WIDTH = PAGE_WIDTH - ACTION_LEFT_MARGIN - ACTION_RIGHT_MARGIN;
    public static final float CHARACTER_BLOCK_WIDTH = PAGE_WIDTH - CHARACTER_LEFT_MARGIN - CHARACTER_RIGHT_MARGIN;
    public static final float PARENTHETICAL_BLOCK_WIDTH = PAGE_WIDTH - PARENTHETICAL_LEFT_MARGIN - PARENTHETICAL_RIGHT_MARGIN;
    public static final float DIALOGUE_BLOCK_WIDTH = PAGE_WIDTH - DIALOGUE_LEFT_MARGIN - DIALOGUE_RIGHT_MARGIN;
    public static final float TRANSITION_BLOCK_WIDTH = PAGE_WIDTH - TRANSITION_LEFT_MARGIN - TRANSITION_RIGHT_MARGIN;
}
