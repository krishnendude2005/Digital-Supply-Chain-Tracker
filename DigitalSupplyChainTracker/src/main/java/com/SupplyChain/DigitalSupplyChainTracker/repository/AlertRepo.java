package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepo extends JpaRepository<Alert, Long> {
    List<Alert> findAllByCheckpointLog_Shipment_ShipmentId(UUID shipmentId);

    //this method - prevents duplicate alerts every time cron runs.
    boolean existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(UUID shipmentId, AlertType type);
}