package com.SupplyChain.DigitalSupplyChainTracker.entity;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID alertId; // Java(accessible) Side ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkpoint_log_id")
    private CheckpointLog checkpointLog;

    @CreatedDate
    private LocalDateTime createdAt;

    private AlertType type;
    private String message;

    @Builder.Default
    private Boolean resolved = false;

}
