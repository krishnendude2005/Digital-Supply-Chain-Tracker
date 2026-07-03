package com.SupplyChain.DigitalSupplyChainTracker.dto.request;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCheckpointLogRequest {
    private UUID shipmentId;
    private String location;
    private ItemStatus itemStatus;
}
