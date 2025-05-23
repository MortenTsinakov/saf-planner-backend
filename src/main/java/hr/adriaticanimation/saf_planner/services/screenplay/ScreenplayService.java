package hr.adriaticanimation.saf_planner.services.screenplay;

import hr.adriaticanimation.saf_planner.dtos.screenplay.CreateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.DeleteScreenplayResponse;
import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayExportData;
import hr.adriaticanimation.saf_planner.dtos.screenplay.ScreenplayResponse;
import hr.adriaticanimation.saf_planner.dtos.screenplay.UpdateScreenplayRequest;
import hr.adriaticanimation.saf_planner.dtos.screenplay.UpdateScreenplayResponse;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.screenplay.Screenplay;
import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayBlock;
import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayContent;
import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayElement;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ScreenplayException;
import hr.adriaticanimation.saf_planner.mappers.screenplay.ScreenplayMapper;
import hr.adriaticanimation.saf_planner.repositories.screenplay.ScreenplayRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.project.ProjectService;
import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.ActionBlockProperties;
import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.BlockProperties;
import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.CharacterBlockProperties;
import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.DialogueBlockProperties;
import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.HeadingBlockProperties;
import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.ParentheticalBlockProperties;
import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.TransitionBlockProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenplayService {

    private final ScreenplayRepository screenplayRepository;
    private final ScreenplayMapper screenplayMapper;
    private final ProjectService projectService;
    private final AuthenticationService authenticationService;

    @Value("${uploads.directory}")
    private String uploadDirectory;

    /**
     * Get the screenplay for a project. If no screenplay has been created yet,
     * return a template for a screenplay that the user can develop further.
     *
     * @param id - project id
     * @return - screenplay for the project
     */
    public ResponseEntity<ScreenplayResponse> getScreenplayForProject(Long id) {
        // The check whether the project belongs to the user is done in
        // ProjectService class.
        Project project = projectService.getUserProjectById(id);
        Screenplay screenplay = screenplayRepository.getScreenplayByProject(project)
                .orElse(getEmptyScreenplay(project));
        ScreenplayResponse response = screenplayMapper.screenplayToScreenplayResponse(screenplay);
        return ResponseEntity.ok(response);
    }

    /**
     * Create an empty screenplay template. It can be used as a starting
     * point for a user who haven't saved any screenplays before.
     *
     * @param project - project where the (potential) screenplay belongs.
     * @return - template for screenplay (Screenplay object)
     */
    private Screenplay getEmptyScreenplay(Project project) {
        ScreenplayElement el = new ScreenplayElement("fade in:");
        ScreenplayBlock bl = ScreenplayBlock.builder()
                .type("header")
                .children(List.of(el))
                .build();
        ScreenplayContent content = ScreenplayContent.builder()
                .id(1L)
                .type("screenplay")
                .children(List.of(bl))
                .build();

        return Screenplay.builder()
                .content(content)
                .project(project)
                .build();
    }

    /**
     * Save new screenplay to the database.
     *
     * @param request - request containing the project id and content of the screenplay
     * @return - created screenplay
     */
    public ResponseEntity<ScreenplayResponse> createScreenplay(CreateScreenplayRequest request) {
        Project project = projectService.getUserProjectById(request.projectId());
        Screenplay screenplay = screenplayMapper.createScreenplayRequestToScreenplay(request, project);
        screenplayRepository.save(screenplay);
        ScreenplayResponse response = screenplayMapper.screenplayToScreenplayResponse(screenplay);
        return ResponseEntity.ok(response);
    }

    /**
     * Update screenplay with given id. Overwrite the current content with the content
     * provided.
     *
     * @param request - request containing the screenplay id and new content
     * @return - message indicating a successful update (NB! The screenplay itself is not provided with the response)
     */
    public ResponseEntity<UpdateScreenplayResponse> updateScreenplay(UpdateScreenplayRequest request) {
        Screenplay screenplay = screenplayRepository.getScreenplayById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Screenplay with given id not found"));
        User user = authenticationService.getUserFromSecurityContextHolder();
        if (!screenplay.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Screenplay with given id not found");
        }

        screenplay.setContent(request.content());
        screenplay.setUpdatedAt(Timestamp.from(Instant.now()));
        screenplayRepository.save(screenplay);

        UpdateScreenplayResponse response = new UpdateScreenplayResponse(screenplay.getId(), "Screenplay was successfully updated");

        return ResponseEntity.ok(response);
    }

    /**
     * Delete screenplay with given id.
     *
     * @param id - id of the screenplay to be deleted
     * @return - message indicating a successful delete operation
     */
    public ResponseEntity<DeleteScreenplayResponse> deleteScreenplay(Long id) {
        Screenplay screenplay = screenplayRepository.getScreenplayById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screenplay with given id was not found"));
        User user = authenticationService.getUserFromSecurityContextHolder();
        if (!screenplay.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Screenplay with given id was not found");
        }

        screenplayRepository.delete(screenplay);

        DeleteScreenplayResponse response = new DeleteScreenplayResponse(id, "Screenplay was successfully deleted");
        return ResponseEntity.ok(response);
    }

    /**
     * Export screenplay as PDF
     * @param data - contains screenplay id and title page data
     */
    public ResponseEntity<byte[]> exportScreenplay(ScreenplayExportData data) {
        Screenplay screenplay = screenplayRepository.getScreenplayById(data.id())
                .orElseThrow(() -> new ResourceNotFoundException("Screenplay with given id was not found"));
        User user = authenticationService.getUserFromSecurityContextHolder();
        if (!screenplay.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Screenplay with given id was not found");
        }

        try (PDDocument document = generatePDF(screenplay, data); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.save(outputStream);
            document.close();
            byte[] pdfBytes = outputStream.toByteArray();
            outputStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", "screenplay.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ScreenplayException("Could not export screenplay");
        }
    }

    /**
     * Generate a PDF document from the Screenplay provided.
     * @param screenplay - Screenplay object containing the content to be exported to PDF.
     * @return - PDF document containing the content of the Screenplay provided.
     * @throws IOException - if the PDF document could not be generated.
     */
    private PDDocument generatePDF(Screenplay screenplay, ScreenplayExportData data) throws IOException {
        PDDocument document = new PDDocument();
        addTitlePage(data, document);
        addPages(screenplay, document);
        return document;
    }

    /**
     * Add the title page to the document provided.
     * @param data - contains screenplay id and title page data.
     * @param document - PDF document to which the title page will be added.
     */
    private void addTitlePage(ScreenplayExportData data, PDDocument document) throws IOException{
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        if (data.title() != null && !data.title().isBlank()) {
            String title = data.title().toUpperCase();
            float lineWidth = ScreenplayConstants.FONT.getStringWidth(title) / 1000 * ScreenplayConstants.FONT_SIZE;
            float x = (ScreenplayConstants.PAGE_WIDTH / 2) - (lineWidth / 2);
            float y = ScreenplayConstants.PAGE_HEIGHT / 2 + ScreenplayConstants.LINE_HEIGHT * 2;
            writeLine(title, contentStream, x, y);
            drawUnderline(contentStream, x, x + lineWidth, y);
        }
        if (data.author() != null && !data.author().isBlank()) {
            String by = "by";
            float lineWidth = ScreenplayConstants.FONT.getStringWidth(by) / 1000 * ScreenplayConstants.FONT_SIZE;
            float x = (ScreenplayConstants.PAGE_WIDTH / 2) - (lineWidth / 2);
            float y = ScreenplayConstants.PAGE_HEIGHT / 2 + ScreenplayConstants.LINE_HEIGHT;
            writeLine(by, contentStream, x, y);

            lineWidth = ScreenplayConstants.FONT.getStringWidth(data.author()) / 1000 * ScreenplayConstants.FONT_SIZE;
            x = (ScreenplayConstants.PAGE_WIDTH / 2) - (lineWidth / 2);
            y = ScreenplayConstants.PAGE_HEIGHT / 2;
            writeLine(data.author(), contentStream, x, y);
        }
        if (data.email() != null || data.phoneNumber() != null) {
            float x = ScreenplayConstants.MARGIN_LEFT;
            float y = ScreenplayConstants.MARGIN_BOTTOM + ScreenplayConstants.LINE_HEIGHT;
            if (data.phoneNumber() != null && !data.phoneNumber().isBlank()) {
                writeLine(data.phoneNumber(), contentStream, x, y);
                y += ScreenplayConstants.LINE_HEIGHT;
            }
            if (data.email() != null && !data.email().isBlank()) {
                writeLine(data.email(), contentStream, x, y);
            }
        }
        if (data.date() != null) {
            float lineWidth = ScreenplayConstants.FONT.getStringWidth(data.date()) / 1000 * ScreenplayConstants.FONT_SIZE;
            float x = ScreenplayConstants.PAGE_WIDTH - ScreenplayConstants.MARGIN_RIGHT - lineWidth;
            float y = ScreenplayConstants.MARGIN_BOTTOM + ScreenplayConstants.LINE_HEIGHT;
            writeLine(data.date(), contentStream, x, y);
        }

        contentStream.close();
    }

    private void writeLine(String line, PDPageContentStream contentStream, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(ScreenplayConstants.FONT, ScreenplayConstants.FONT_SIZE);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(line);
        contentStream.endText();
    }

    private void drawUnderline(PDPageContentStream contentStream, float sx, float ex, float y) throws IOException {
        float lineWidth = 1f;
        float deltaY = 3f;
        contentStream.setLineWidth(lineWidth);
        contentStream.moveTo(sx, y - deltaY);
        contentStream.lineTo(ex, y - deltaY);
        contentStream.stroke();
    }

    /**
     * Divide the Screenplay content into pages and add them to the document provided.
     */
    private void addPages(Screenplay screenplay, PDDocument document) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        Integer pageNr = 1;
        document.addPage(page);

        List<ScreenplayLine> lines = contentToLines(screenplay.getContent());

        float yPosition = ScreenplayConstants.PAGE_HEIGHT - ScreenplayConstants.MARGIN_TOP;

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        for (ScreenplayLine line : lines) {
            BlockProperties props = line.getProperties();
            String text = line.getLine();

            // Create new page if the line is not on page anymore
            if (yPosition - ScreenplayConstants.LINE_HEIGHT < ScreenplayConstants.MARGIN_BOTTOM) {
                contentStream.close();
                pageNr++;

                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                writePageNumber(contentStream, pageNr);
                yPosition = ScreenplayConstants.PAGE_HEIGHT - ScreenplayConstants.MARGIN_TOP;
            }

            // Write line
            contentStream.beginText();
            contentStream.setFont(props.getFont(), props.getFontSize());
            contentStream.newLineAtOffset(props.getMarginLeft(), yPosition);
            contentStream.showText(text);
            contentStream.endText();

            yPosition -= ScreenplayConstants.LINE_HEIGHT;
        }
        contentStream.close();
    }

    /**
     * Write a page number on the page
     */
    private void writePageNumber(PDPageContentStream contentStream, Integer pageNr) throws IOException {
        String text = String.format("%d.", pageNr);
        float lineWidth = ScreenplayConstants.FONT.getStringWidth(text) / 1000 * ScreenplayConstants.FONT_SIZE;

        contentStream.beginText();
        contentStream.setFont(ScreenplayConstants.FONT, ScreenplayConstants.FONT_SIZE);
        contentStream.newLineAtOffset(
                ScreenplayConstants.PAGE_WIDTH - ScreenplayConstants.MARGIN_RIGHT - lineWidth,
                ScreenplayConstants.PAGE_HEIGHT - (ScreenplayConstants.MARGIN_TOP / 2));
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Take a ScreenplayContent object, and delegate the task of cutting the text of each
     * child element to appropriately sized lines.
     */
    private List<ScreenplayLine> contentToLines(ScreenplayContent content) {
        List<ScreenplayLine> lines = new ArrayList<>();
        content.getChildren().forEach(child -> {
            if (!child.getChildren().isEmpty()) {
                try {
                    List<ScreenplayLine> blockLines = null;
                    switch (child.getType().toLowerCase()) {
                        case "header":
                            blockLines = createLines(child.getChildren().getFirst(), new HeadingBlockProperties());
                            break;
                        case "action":
                            blockLines = createLines(child.getChildren().getFirst(), new ActionBlockProperties());
                            break;
                        case "character":
                            blockLines = createLines(child.getChildren().getFirst(), new CharacterBlockProperties());
                            break;
                        case "parenthetical":
                            blockLines = createLines(child.getChildren().getFirst(), new ParentheticalBlockProperties());
                            break;
                        case "dialogue":
                            blockLines = createLines(child.getChildren().getFirst(), new DialogueBlockProperties());
                            break;
                        case "transition":
                            blockLines = createLines(child.getChildren().getFirst(), new TransitionBlockProperties());
                            break;
                        default:
                            break;
                    }
                    if (blockLines != null) {
                        lines.addAll(blockLines);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    throw new ScreenplayException("Exporting PDF failed");
                }
            }
        });

        return lines;
    }

    /**
     * Take the text from inside a screenplay element and wrap the text into appropriately
     * sized lines according to the block properties provided.
     * Return a list of lines as ScreenplayLine objects.
     */
    private List<ScreenplayLine> createLines(ScreenplayElement element, BlockProperties properties) throws IOException {
        List<ScreenplayLine> lines = new ArrayList<>();
        String text = element.getText();
        if (properties instanceof HeadingBlockProperties || properties instanceof CharacterBlockProperties || properties instanceof TransitionBlockProperties) {
            text = text.toUpperCase();
        } else if (properties instanceof ParentheticalBlockProperties) {
            text = "(" + text + ")";
        }
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String currentLine = line.isEmpty() ? word : line + " " + word;
            float size = ScreenplayConstants.FONT.getStringWidth(currentLine) / 1000 * ScreenplayConstants.FONT_SIZE;
            if (size > properties.getBlockWidth()) {
                lines.add(new ScreenplayLine(line.toString(), properties));
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(currentLine);
            }
        }
        lines.add(new ScreenplayLine(line.toString(), properties));
        if (properties instanceof HeadingBlockProperties || properties instanceof ActionBlockProperties || properties instanceof DialogueBlockProperties || properties instanceof TransitionBlockProperties) {
            lines.add(new ScreenplayLine("", properties));
        }
        return lines;
    }
}
