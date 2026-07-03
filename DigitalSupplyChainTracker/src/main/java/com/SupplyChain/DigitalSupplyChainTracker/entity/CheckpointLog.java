package com.SupplyChain.DigitalSupplyChainTracker.entity;


import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckpointLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID checkpointId; // Java(accessible) Side ID

    private String location;

    private ItemStatus itemStatus;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    private LocalDateTime timestamp;

}
