package com.SupplyChain.DigitalSupplyChainTracker.entity;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkpoint_log_id")
    private CheckpointLog checkpointLog;

    private AlertType type;
    private LocalDate createdOn;
    private String message;

}
