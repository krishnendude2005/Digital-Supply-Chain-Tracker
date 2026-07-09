package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CheckpointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;

import java.util.List;
import java.util.UUID;

public interface CheckPointLogService {
    CreateCheckPointLogResponse createCheckPointLog(CreateCheckpointLogRequest checkpointLogRequest);
    List<CheckpointLogResponse> getShipmentLog(UUID shipmentId);
}
