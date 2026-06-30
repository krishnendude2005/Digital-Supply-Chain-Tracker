package com.SupplyChain.DigitalSupplyChainTracker.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ShipmentRequest {
    private UUID itemId;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
