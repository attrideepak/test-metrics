package qa.test_metrics.service;

import qa.test_metrics.model.ReportData;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Reader;

public interface Parser {
    void parse(Reader r, ReportData data) throws IOException, JAXBException;
}
