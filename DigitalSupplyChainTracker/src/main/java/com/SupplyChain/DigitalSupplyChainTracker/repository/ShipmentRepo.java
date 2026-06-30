package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByShipmentId(UUID shipmentId);
}
