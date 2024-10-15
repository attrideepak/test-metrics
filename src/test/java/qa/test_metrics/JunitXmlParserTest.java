package qa.test_metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import qa.test_metrics.entities.Scenario;
import qa.test_metrics.entities.SuiteResult;
import qa.test_metrics.model.ReportData;
import qa.test_metrics.repository.FeatureRepository;
import qa.test_metrics.repository.ScenarioRepository;
import qa.test_metrics.repository.SuiteResultRepository;
import qa.test_metrics.service.junit.JunitXmlParser;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class JunitXmlParserTest {

    @Mock
    private SuiteResultRepository suiteResultRepository;

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private FeatureRepository featureRepository;

    @InjectMocks
    private JunitXmlParser junitXmlParser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldThrowExceptionForEmptyXmlContent() {
        // Arrange
        Reader reader = new StringReader(""); // Empty XML
        SuiteResult suiteResult = new SuiteResult("param1", "param2", "param3", "param4", BigDecimal.ZERO);
        ReportData reportData = new ReportData("param1", "param2", suiteResult);
        reportData.setSuiteResult(suiteResult);

        // Act and Assert
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            junitXmlParser.parse(reader, reportData);
        });

        // Verify that no saves were attempted due to empty content
        verify(suiteResultRepository, never()).save(any());
        verify(scenarioRepository, never()).save(any());
    }

    @Test
    public void shouldHandleNullReader() {
        // Arrange
        SuiteResult suiteResult = new SuiteResult("param1", "param2", "param3", "param4", BigDecimal.ZERO);
        ReportData reportData = new ReportData("param1", "param2", suiteResult);
        reportData.setSuiteResult(suiteResult);

        // Act and Assert
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            junitXmlParser.parse(null, reportData);
        });

        // Verify no repository interactions due to null reader
        verify(suiteResultRepository, never()).save(any());
    }

    private String getValidJunitXml() {
        return """
                             <testsuites>
                               <testsuite ignored="0" hostname="runner-gu57wnqj-project-6462-concurrent-0-u0zdvt4p" failures="0" tests="3" name="dakota.app.api.tests.corebanking.payment.PayNowRegistrationDeregistrationTests" time="123.495" errors="0" timestamp="2024-10-08T03:23:16 GMT" skipped="0">
                                 <testcase features="DAKOTA-6514" classname="dakota.app.api.tests.corebanking.payment.PayNowRegistrationDeregistrationTests" name="shouldBeAbleToLookupPaynowProfileRegisteredWithExternalBank" time="5.398"/>
                                 <system-out/>
                                 <testcase features="DAKOTA-2480" classname="dakota.app.api.tests.corebanking.payment.PayNowRegistrationDeregistrationTests" name="shouldThrowAnErrorWhenDeregisteringOtherUserAccountFromPaynow" time="5.855"/>
                                 <system-out/>
                                 <testcase features="DAKOTA-6514" classname="dakota.app.api.tests.corebanking.payment.PayNowRegistrationDeregistrationTests" name="shouldThrowAnErrorForProfileLookupIfUserIsDeregisteredFromPayNow" time="6.724"/>
                                 <system-out/>
                               </testsuite>
                             </testsuites>
                
               """;
    }
}