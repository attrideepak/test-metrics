package qa.test_metrics.service.junit.reportmodel;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@XmlRootElement(name = "testsuite")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class JunitTestSuite {

    @XmlAttribute(name = "tests")
    private int tests;

    @XmlAttribute(name = "skipped")
    private int skipped;

    @XmlAttribute(name = "failures")
    private int failures;

    @XmlAttribute(name = "errors")
    private int errors;

    @XmlAttribute(name = "ignored")
    private int ignored;

    @XmlAttribute(name = "time")
    private double time;

    @XmlElement(name = "testcase")
    private List<JunitTestCase> junitTestCases;
}