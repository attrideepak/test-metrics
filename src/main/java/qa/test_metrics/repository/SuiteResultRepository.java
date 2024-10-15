package qa.test_metrics.repository;

import qa.test_metrics.entities.SuiteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuiteResultRepository extends JpaRepository<SuiteResult, Long> {
}
