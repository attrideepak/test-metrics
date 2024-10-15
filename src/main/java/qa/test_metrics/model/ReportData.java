package qa.test_metrics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import qa.test_metrics.entities.SuiteResult;

@Data
@AllArgsConstructor
public class ReportData {
    private String jira;
    private String reportFormat;
    private SuiteResult suiteResult;
}
