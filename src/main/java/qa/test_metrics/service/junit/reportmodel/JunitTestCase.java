package qa.test_metrics.service.junit.reportmodel;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@XmlRootElement(name = "testcase")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class JunitTestCase {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "classname")
    private String className;

    @XmlAttribute(name = "time")
    private BigDecimal time;

    @XmlAttribute(name = "features")
    private String features;

    @XmlElement(name = "failure")
    private Object failure;

    @XmlElement(name = "skipped")
    private Object skipped;

    @XmlElement(name = "error")
    private Object error;

    public String getFeatures() {
        return features != null ? features : "";
    }
}