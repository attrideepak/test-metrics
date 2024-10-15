package qa.test_metrics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qa.test_metrics.model.ReportData;
import qa.test_metrics.entities.SuiteResult;
import qa.test_metrics.repository.SuiteResultRepository;

import java.io.Reader;
import java.math.BigDecimal;

@Service
public class ReportProcessor {

    @Autowired
    private ReportParser reportParser;

    @Autowired
    private SuiteResultRepository suiteResultRepository;

    public void process(String build, String environment, String jira, String reportFormat, String service,
                               String testType, BigDecimal coverage, Reader reader) throws Exception {

        // Create base result (equivalent of `model.Data`)
        ReportData data = new ReportData(jira, reportFormat, new SuiteResult(build, environment, service, testType,
                coverage));

        // Transform file data into the required format
        try {
           reportParser.parse(reader, data);
        } catch (Exception e) {
            throw new Exception("Error parsing report data", e);
        }

    }




}
