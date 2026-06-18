package com.SupplyChain.DigitalSupplyChainTracker.entity;


import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CheckpointLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String location;

    private ItemStatus itemStatus;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    private LocalDateTime timestamp;

}
