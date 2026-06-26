package com.SupplyChain.DigitalSupplyChainTracker.entity;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String alertId; // Java(accessible) Side ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkpoint_log_id")
    private CheckpointLog checkpointLog;

    private LocalDateTime createdAt;

    private AlertType type;
    private LocalDate createdOn;
    private String message;

}
