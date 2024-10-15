package qa.test_metrics.repository;

import qa.test_metrics.entities.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    Scenario findByNameAndClassNameAndTestTypeAndService(String name, String className, String testType, String service);
}
