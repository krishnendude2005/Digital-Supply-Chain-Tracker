package com.SupplyChain.DigitalSupplyChainTracker.dto.response;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private UUID shipmentId;
    private ItemResponse item;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime shipmentStartDate;
    private LocalDateTime shipmentExpectedDate;
    private ShipmentStatus currentStatus;
    private String transporterEmail;
    private String transporterName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
