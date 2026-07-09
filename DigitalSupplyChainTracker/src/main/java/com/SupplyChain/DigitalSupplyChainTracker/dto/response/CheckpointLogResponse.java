package com.SupplyChain.DigitalSupplyChainTracker.dto.response;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
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
public class CheckpointLogResponse {
    private UUID checkpointId;
    private String location;
    private ItemStatus itemStatus;
    private UUID shipmentId;
    private LocalDateTime timestamp;
}
