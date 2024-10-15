package qa.test_metrics.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublishReportResponse {
    private String message;
    private String status;
}
