package com.SupplyChain.DigitalSupplyChainTracker.dto.request;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentStatusChangeRequest {
    private ShipmentStatus status;
}
