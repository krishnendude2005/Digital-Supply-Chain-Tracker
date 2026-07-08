package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckpointLogRepo extends JpaRepository<CheckpointLog, Long> {
    List<CheckpointLog> findAllByShipment_ShipmentId(UUID shipmentId);

    //method to get latest checkpointLog for a shipment
    Optional<CheckpointLog> findTopByShipment_ShipmentIdOrderByTimestampDesc(UUID shipmentId);
}

