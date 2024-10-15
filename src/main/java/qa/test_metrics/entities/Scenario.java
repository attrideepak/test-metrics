package qa.test_metrics.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "scenarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "className", "testType", "service"})
})
@NoArgsConstructor
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String testType;

    @Column(nullable = false)
    private String service;

    @ManyToMany
    @JoinTable(
            name = "feature_scenarios",  // Join table for many-to-many relationship
            joinColumns = @JoinColumn(name = "scenario_id"),  // Foreign key for Scenario
            inverseJoinColumns = @JoinColumn(name = "feature_id")  // Foreign key for Feature
    )
    private List<Feature> features;

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

    public Scenario(String name, String className, String testType, String service, List<Feature> features) {
        this.name = name;
        this.className = className;
        this.testType = testType;
        this.service = service;
        this.features = features;
    }
}
