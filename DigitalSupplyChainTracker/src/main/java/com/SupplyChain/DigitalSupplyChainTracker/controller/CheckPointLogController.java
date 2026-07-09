package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CheckpointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.service.CheckPointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/checkpoints")
@RequiredArgsConstructor
public class CheckPointLogController {

    private final CheckPointLogService checkPointLogService;

    @PostMapping()
    public ResponseEntity<?> createCheckpoint(@RequestBody CreateCheckpointLogRequest checkpointLogRequest) {

        CreateCheckPointLogResponse response = checkPointLogService.createCheckPointLog(checkpointLogRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<?> getShipmentLog(@PathVariable UUID shipmentId) {
        List<CheckpointLogResponse> logs = checkPointLogService.getShipmentLog(shipmentId);
        return ResponseEntity.status(HttpStatus.OK).body(logs);
    }
}
