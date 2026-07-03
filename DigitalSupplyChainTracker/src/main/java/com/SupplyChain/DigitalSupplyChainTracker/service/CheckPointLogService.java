package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;

public interface CheckPointLogService {
    CreateCheckPointLogResponse createCheckPointLog(CreateCheckpointLogRequest checkpointLogRequest);
}
