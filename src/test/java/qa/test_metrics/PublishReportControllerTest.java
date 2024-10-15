package qa.test_metrics;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import qa.test_metrics.service.ReportProcessor;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class PublishReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportProcessor reportProcessor;

    @Test
    void shouldPublishReportSuccessfully() throws Exception {
        // Mock the reportProcessor behavior
        Mockito.doNothing().when(reportProcessor).process(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(BigDecimal.class),
                Mockito.any()
        );

        // Prepare multipart file
        MockMultipartFile file = new MockMultipartFile(
                "report_file",
                "test-report.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<testsuites></testsuites>".getBytes()
        );

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/publish/report")
                        .file(file)
                        .param("ci_job_id", "12345")
                        .param("environment", "prod")
                        .param("jira_project", "JIRA123")
                        .param("report_format", "junit")
                        .param("service", "test-service")
                        .param("test_type", "unit")
                        .param("coverage", "85")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Report published successfully"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldReturnBadRequestForInvalidFormat() throws Exception {
        // Prepare multipart file
        MockMultipartFile file = new MockMultipartFile(
                "report_file",
                "test-report.json",
                MediaType.APPLICATION_JSON_VALUE,
                "{}".getBytes()
        );

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/publish/report")
                        .file(file)
                        .param("ci_job_id", "12345")
                        .param("environment", "prod")
                        .param("jira_project", "JIRA123")
                        .param("report_format", "json")  // Invalid format
                        .param("service", "test-service")
                        .param("test_type", "unit")
                        .param("coverage", "85")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid report format, report format should be: [junit]"))
                .andExpect(jsonPath("$.status").value("failed"));
    }

    @Test
    void shouldReturnBadRequestForInvalidTestTypes() throws Exception {
        // Prepare multipart file
        MockMultipartFile file = new MockMultipartFile(
                "report_file",
                "test-report.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<testsuites></testsuites>".getBytes()
        );

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/publish/report")
                        .file(file)
                        .param("ci_job_id", "12345")
                        .param("environment", "prod")
                        .param("jira_project", "JIRA123")
                        .param("report_format", "junit")
                        .param("service", "test-service")
                        .param("test_type", "service") //Invalid testtype
                        .param("coverage", "85")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid test type, test type should be: [unit, ui, integration, e2e]"))
                .andExpect(jsonPath("$.status").value("failed"));
    }

    @Test
    void shouldReturnBadRequestIfFileNameDoesNotEndWithXml() throws Exception {
        // Prepare multipart file
        MockMultipartFile file = new MockMultipartFile(
                "report_file",
                "test-report.json",
                MediaType.APPLICATION_XML_VALUE,
                "<testsuites></testsuites>".getBytes()
        );

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/publish/report")
                        .file(file)
                        .param("ci_job_id", "12345")
                        .param("environment", "prod")
                        .param("jira_project", "JIRA123")
                        .param("report_format", "junit")
                        .param("service", "test-service")
                        .param("test_type", "unit")
                        .param("coverage", "85")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Report is not in XML format"))
                .andExpect(jsonPath("$.status").value("failed"));
    }
}


