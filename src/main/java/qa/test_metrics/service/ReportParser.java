package qa.test_metrics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qa.test_metrics.model.ReportData;
import qa.test_metrics.service.junit.JunitXmlParser;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Reader;

@Service
public class ReportParser {

    private static final Logger log = LoggerFactory.getLogger(ReportParser.class);

    @Autowired
    private JunitXmlParser junitXmlParser;

    public void parse(Reader r, ReportData data) {
        Parser parser;
        String reportFormat = data.getReportFormat().toLowerCase();

        switch (reportFormat) {
            case "junit":
                parser = junitXmlParser;
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid report format: %s", reportFormat));
        }

        if (parser != null) {
            try {
                parser.parse(r, data);
            } catch (JAXBException | IOException e) {
                log.error("Error parsing report data", e);
                throw new RuntimeException("Error parsing report data");
            }
        }
    }
}