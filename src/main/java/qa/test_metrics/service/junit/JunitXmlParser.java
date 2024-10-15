package qa.test_metrics.service.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qa.test_metrics.entities.Feature;
import qa.test_metrics.entities.Scenario;
import qa.test_metrics.entities.ScenarioResult;
import qa.test_metrics.entities.SuiteResult;
import qa.test_metrics.model.*;
import qa.test_metrics.repository.FeatureRepository;
import qa.test_metrics.repository.ScenarioRepository;
import qa.test_metrics.repository.SuiteResultRepository;
import qa.test_metrics.service.Parser;
import qa.test_metrics.service.junit.reportmodel.JunitReport;
import qa.test_metrics.service.junit.reportmodel.JunitTestCase;
import qa.test_metrics.service.junit.reportmodel.JunitTestSuite;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JunitXmlParser implements Parser {

    @Autowired
    private SuiteResultRepository suiteResultRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private FeatureRepository featureRepository;

    private static final Logger log = LoggerFactory.getLogger(JunitXmlParser.class);

    private String readfile(Reader reader) throws IOException {
        log.info("Reading file content");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Transactional
    public void parse(Reader reader, ReportData data) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }

        SuiteResult suiteResult = data.getSuiteResult();

        String reportContent;
        try {
            reportContent = readfile(reader);
        } catch (IOException e) {
            log.error("Error reading the file", e);
            throw new RuntimeException("Failed to read the file content", e);
        }

        if (reportContent.isEmpty()) {
            log.error("Report content is null or empty");
            throw new RuntimeException("Failed to read the file content");
        }

        JunitReport junitReport = new JunitReport();

        log.info("Unmarshalling to JUnit report");
        JAXBContext jaxbContext;
        Unmarshaller unmarshaller;

        try {
            if (reportContent.contains("testsuites")) {
                jaxbContext = JAXBContext.newInstance(JunitReport.class);
            } else {
                jaxbContext = JAXBContext.newInstance(JunitTestSuite.class);
            }

            unmarshaller = jaxbContext.createUnmarshaller();
            if (reportContent.contains("testsuites")) {
                junitReport = (JunitReport) unmarshaller.unmarshal(new StringReader(reportContent));
            } else {
                JunitTestSuite junitTestSuite = (JunitTestSuite) unmarshaller.unmarshal(new StringReader(reportContent));
                junitReport.getTestSuites().add(junitTestSuite);
            }
        } catch (JAXBException e) {
            log.error("Error during unmarshalling", e);
            throw new RuntimeException("Error during unmarshalling", e);
        }

        log.info("Unmarshalling done successfully");

        // Process the results
        processTestSuites(junitReport.getTestSuites(), suiteResult, data.getJira());

    }

    private void processTestSuites(List<JunitTestSuite> testSuites, SuiteResult suiteResult, String jiraProject) {
        log.info("Processing test suites");
        double totalTime = 0;
        for (JunitTestSuite suite : testSuites) {
            suiteResult.setTotalExecuted(suiteResult.getTotalExecuted() + suite.getTests());
            suiteResult.setTotalFailed(suiteResult.getTotalFailed() + suite.getFailures() + suite.getErrors());
            suiteResult.setTotalIgnored(suiteResult.getTotalIgnored() + suite.getIgnored());
            suiteResult.setTotalSkipped(suiteResult.getTotalSkipped() + suite.getSkipped() + suite.getIgnored());
            suiteResult.setTotalPassed(suiteResult.getTotalPassed() + suite.getTests()
                    - (suite.getFailures() + suite.getSkipped() + suite.getErrors() + suite.getIgnored()));

            totalTime += suite.getTime();
            suiteResult.setTimeTaken(BigDecimal.valueOf(totalTime));

        }

        addScenarioResults(testSuites, suiteResult);
        processScenarioResults(suiteResult, jiraProject);
        suiteResultRepository.save(suiteResult);
    }

    private void addScenarioResults(List<JunitTestSuite> testSuites, SuiteResult suiteResult) {
        List<ScenarioResult> scenarioResults = new ArrayList<>();
        for (JunitTestSuite suite : testSuites) {
            for (JunitTestCase testCase : suite.getJunitTestCases()) {
                String status = "passed";
                if (testCase.getFailure() != null || testCase.getError() != null) {
                    status = "failed";
                } else if (testCase.getSkipped() != null) {   //ignored are considered as skipped in junit report
                    status = "skipped";
                }

                ScenarioResult scenarioResult = new ScenarioResult(
                        testCase.getName(),
                        testCase.getClassName(),
                        status,
                        testCase.getTime(),
                        testCase.getFeatures().split(" ")
                );

                scenarioResult.setSuiteResult(suiteResult);
                scenarioResults.add(scenarioResult);
            }
            suiteResult.setScenarioResults(scenarioResults);
        }
    }



    private void processScenarioResults(SuiteResult suiteResult, String jira) {
        log.info(jira);
        List<ScenarioResult> scenarioResults = suiteResult.getScenarioResults();
        log.info("Processing scenarios");

        for (ScenarioResult scenarioResult : scenarioResults) {
            List<Feature> features = getFeaturesFromScenarioResult(scenarioResult, jira);
            for (Feature feature : features) {
                featureRepository.save(feature);// Save each feature
                log.info("Feature saved");
            }

            Scenario existingScenario = scenarioRepository.findByNameAndClassNameAndTestTypeAndService(
                    scenarioResult.getName(),
                    scenarioResult.getClassName(),
                    suiteResult.getTestType(),
                    suiteResult.getService()
            );

            Scenario scenario;
            if (existingScenario != null) {
                // Update existing scenario
                existingScenario.setFeatures(features);
                scenario = scenarioRepository.save(existingScenario);
            } else {
                // Create new scenario
                scenario = new Scenario(
                        scenarioResult.getName(),
                        scenarioResult.getClassName(),
                        suiteResult.getTestType(),
                        suiteResult.getService(),
                        features
                );
                scenario = scenarioRepository.save(scenario);
            }

            // Set scenarioId in ScenarioResult
            scenarioResult.setScenarioId(scenario.getId());
        }
    }

    public List<Feature> getFeaturesFromScenarioResult(ScenarioResult r, String projectName) {
        String pat = "(?i)" + projectName + "-\\d+";
        Pattern pattern = Pattern.compile(pat);
        List<Feature> features = new ArrayList<>(r.getFeatures().length);

        for (String f : r.getFeatures()) {
            f = f.toUpperCase();
            Matcher matcher = pattern.matcher(f);
            if (matcher.find()) {
                features.add(new Feature(f));
            }
        }
        return features;
    }
}
