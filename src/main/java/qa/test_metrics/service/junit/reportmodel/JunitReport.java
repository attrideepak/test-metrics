package qa.test_metrics.service.junit.reportmodel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "testsuites")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class JunitReport {

    @XmlElement(name = "testsuite")
    private List<JunitTestSuite> testSuites;
}