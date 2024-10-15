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
@Table(name = "features")
@NoArgsConstructor
public class Feature {

    @Id
    @Column(nullable = false, unique = true)
    private String id;  // Corresponds to string ID in Go

    @Column
    private String title;

    @ManyToMany(mappedBy = "features")  // Indicates the owner side is in Scenario
    private List<Scenario> scenarios;  // The list of scenarios associated with this feature

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

    public Feature(String id) {
        this.id = id;
    }

}
