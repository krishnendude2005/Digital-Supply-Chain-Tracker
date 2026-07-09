package com.SupplyChain.DigitalSupplyChainTracker.dto.response;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
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
public class ItemResponse {
    private UUID itemId;
    private String name;
    private String category;
    private String supplierEmail;
    private String supplierName;
    private Role supplierRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
