package qa.test_metrics.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Getter
@Setter
@Entity
@Table(name = "scenario_results")
public class ScenarioResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // Corresponds to uint in Go

    @Column(nullable = false)
    private int scenarioId;  // Corresponds to ScenarioID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suite_result_id", nullable = false)
    private SuiteResult suiteResult;  // Assuming SuiteResult is another entity class

    @Transient
    private String name;

    @Transient
    private String className;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, precision = 7, scale = 2, columnDefinition = "decimal(7,2) default 0")
    private BigDecimal timeTaken;

    @Transient
    private String[] features;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ScenarioResult(String name, String className, String status, BigDecimal timeTaken, String[] features) {
        this.name = name;
        this.className = className;
        this.status = status;
        this.timeTaken = timeTaken != null ? timeTaken : BigDecimal.ZERO;
        this.features = features;
    }
}
