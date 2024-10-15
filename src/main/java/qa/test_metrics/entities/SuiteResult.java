package qa.test_metrics.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


//@Data annotation is not used because it generates the toString/equalsAndhashCode method which causes a stack
// overflow error/cyclic dependency
@Getter
@Setter
@Entity
@Table(name = "suite_results", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"build", "testType"})
})
public class SuiteResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // Using Long instead of uint

    @Column(nullable = false)
    private String build;

    @Column(nullable = false)
    private String testType;

    @Column(nullable = false)
    private String service;

    @Column(nullable = false)
    private String environment;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal timeTaken;  // Using double for float64

    @Column(nullable = false, columnDefinition = "int default 0")
    private int totalExecuted;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int totalPassed;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int totalFailed;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int totalSkipped;

    @Transient
    private int totalIgnored;

    @Column(nullable = false, precision = 5, scale = 2)  // Precision 5, scale 2 (99.99)
    private BigDecimal coverage;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "suiteResult")
    private List<ScenarioResult> scenarioResults;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public SuiteResult(String build, String environment, String service, String testype, BigDecimal coverage) {
        this.build = build;
        this.environment = environment;
        this.service = service;
        this.testType = testype;
        this.coverage = coverage;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
