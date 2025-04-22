package hr.adriaticanimation.saf_planner.dtos.screenplay;

public record ScreenplayExportData(
        Long id,
        String title,
        String author,
        String email,
        String phoneNumber,
        String date
) {}
