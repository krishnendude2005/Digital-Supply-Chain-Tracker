package com.SupplyChain.DigitalSupplyChainTracker.scheduler.service;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import com.SupplyChain.DigitalSupplyChainTracker.exception.CheckpointLogNotFound;
import com.SupplyChain.DigitalSupplyChainTracker.repository.AlertRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.CheckpointLogRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentDelaySchedulerService {

    private final ShipmentRepo shipmentRepo;
    private final AlertRepo alertRepo;
    private final CheckpointLogRepo checkpointLogRepo;

    @Transactional
    public void scanAndCreateShipmentDelayAlerts() {

        LocalDateTime now = LocalDateTime.now();

        //get all the delayed shipments
        List<Shipment> delayedShipments = shipmentRepo.findByShipmentExpectedDateBeforeAndCurrentStatus(now, ShipmentStatus.IN_TRANSIT);

        //check if alert already not present for a shipment . IF NOT->create one according to its latest CheckpointLog
        for(Shipment shipment : delayedShipments) {
            if(!alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(shipment.getShipmentId(), AlertType.DELAYED)) {

                //find its latest checkpointLog details
                CheckpointLog  checkpointLog = checkpointLogRepo.findTopByShipment_ShipmentIdOrderByTimestampDesc(shipment.getShipmentId())
                        .orElseThrow(() -> new CheckpointLogNotFound("Shipment Not Started Yet for Shipment with ID" + shipment.getShipmentId()));

                //create alert
                Alert newAlert = Alert.builder()
                        .alertId(UUID.randomUUID())
                        .checkpointLog(checkpointLog)
                        .type(AlertType.DELAYED)
                        .message("Shipment Delayed")
                        .build();

                alertRepo.save(newAlert);

            }
        }

    }
}
