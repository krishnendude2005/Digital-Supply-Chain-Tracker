package com.SupplyChain.DigitalSupplyChainTracker.dto.response;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
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
public class AlertResponse {
    private UUID alertId;
    private AlertType type;
    private String message;
    private Boolean resolved;
    private UUID checkpointId;
    private UUID shipmentId;
    private LocalDateTime createdAt;
}
