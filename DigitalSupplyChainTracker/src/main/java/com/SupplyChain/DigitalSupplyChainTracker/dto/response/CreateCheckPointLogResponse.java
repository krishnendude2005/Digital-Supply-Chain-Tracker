package com.SupplyChain.DigitalSupplyChainTracker.dto.response;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCheckPointLogResponse {
    private String message;
    private UUID shipmentId;
    private String location;
    private ItemStatus itemStatus;
    private LocalDateTime date;
}
