package com.SupplyChain.DigitalSupplyChainTracker.entity;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private String fromLocation;
    private String toLocation;
    private LocalDate shipmentStartDate;
    private LocalDate shipmentExpectedDate;
    private ShipmentStatus currentStatus;
}
