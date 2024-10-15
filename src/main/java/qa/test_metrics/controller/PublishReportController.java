package qa.test_metrics.controller;

import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import qa.test_metrics.model.PublishReportResponse;
import qa.test_metrics.service.ReportProcessor;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@RestController
@Validated
public class PublishReportController {
    @Autowired
    private ReportProcessor reportProcessor;

    List<String> validTestTypes = List.of("unit", "ui", "integration", "e2e");
    List<String> validReportFormats = List.of("junit");

    @PostMapping(value = "v1/publish/report", consumes = "multipart/form-data")
    public ResponseEntity<PublishReportResponse> publishReport(
            @NotBlank(message = "ci_job_id is mandatory")
            @RequestParam("ci_job_id") String build,

            @NotBlank(message = "Environment is mandatory")
            @RequestParam("environment") String environment,

            @NotBlank(message = "jira_project is mandatory")
            @RequestParam("jira_project") String jira,

            @NotBlank(message = "report_format is mandatory")
            @RequestParam("report_format") String reportFormat,

            @NotBlank(message = "service is mandatory")
            @RequestParam("service") String service,

            @NotBlank(message = "test_type is mandatory")
            @RequestParam("test_type") String testType,

            @NotNull(message = "Coverage is mandatory")
            @Min(value = 0, message = "Coverage should be between 0 and 100")
            @Max(value = 100, message = "Coverage should be between 0 and 100")
            @RequestParam("coverage") Double coverage,

            @RequestPart("report_file") MultipartFile file) {

        if (!validReportFormats.contains(reportFormat)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PublishReportResponse("Invalid report format, report format should be: "
                            + validReportFormats, "failed"));
        }

        if (!validTestTypes.contains(testType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PublishReportResponse("Invalid test type, test type should be: "
                            + validTestTypes, "failed"));
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xml")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PublishReportResponse("Report is not in XML format", "failed"));
        }


        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            reportProcessor.process(build, environment, jira, reportFormat, service, testType,
                    BigDecimal.valueOf(coverage), reader);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PublishReportResponse("Error parsing xml report: " + e.getMessage(), "failed"));
        }


        return ResponseEntity.status(HttpStatus.OK)
                .body(new PublishReportResponse("Report published successfully", "success"));
    }
}
