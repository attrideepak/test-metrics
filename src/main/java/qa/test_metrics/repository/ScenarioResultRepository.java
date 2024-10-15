package qa.test_metrics.repository;

import qa.test_metrics.entities.ScenarioResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioResultRepository extends JpaRepository<ScenarioResult, Long> {
}
