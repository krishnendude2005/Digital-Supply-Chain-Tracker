package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CheckpointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ResourceNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.CheckpointLogRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.CheckPointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CheckpointLogServiceImpl implements CheckPointLogService {
    private final ShipmentRepo shipmentRepo;
    private final CheckpointLogRepo checkpointLogRepo;

    @Override
    public CreateCheckPointLogResponse createCheckPointLog(CreateCheckpointLogRequest checkpointLogRequest) {

        //Extract data from request
        UUID shipmentId = checkpointLogRequest.getShipmentId();
        String location = checkpointLogRequest.getLocation();
        ItemStatus itemStatus = checkpointLogRequest.getItemStatus();

        Shipment shipment = shipmentRepo.findByShipmentId(shipmentId).orElseThrow(()-> new ResourceNotFoundException("Shipment not found with id: " + shipmentId + ""));


        CheckpointLog newCheckPointLog = CheckpointLog.builder()
                .checkpointId(UUID.randomUUID())
                .location(location)
                .itemStatus(itemStatus)
                .shipment(shipment)
                .timestamp(LocalDateTime.now())
                .build();

        //Save the checkpoint log in DB
        checkpointLogRepo.save(newCheckPointLog);

        return CreateCheckPointLogResponse.builder()
                .itemStatus(newCheckPointLog.getItemStatus())
                .location(newCheckPointLog.getLocation())
                .shipmentId(newCheckPointLog.getShipment().getShipmentId())
                .location(newCheckPointLog.getLocation())
                .message("Checkpoint Log Created Successfully")
                .build();
    }

    @Override
    public List<CheckpointLogResponse> getShipmentLog(UUID shipmentId) {
        return checkpointLogRepo.findAllByShipment_ShipmentId(shipmentId).stream()
                .map(this::convertToCheckpointLogResponse)
                .toList();
    }

    private CheckpointLogResponse convertToCheckpointLogResponse(CheckpointLog checkpointLog) {
        return CheckpointLogResponse.builder()
                .checkpointId(checkpointLog.getCheckpointId())
                .location(checkpointLog.getLocation())
                .itemStatus(checkpointLog.getItemStatus())
                .shipmentId(checkpointLog.getShipment() != null ? checkpointLog.getShipment().getShipmentId() : null)
                .timestamp(checkpointLog.getTimestamp())
                .build();
    }
}
