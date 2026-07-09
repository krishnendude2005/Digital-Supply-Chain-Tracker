package com.SupplyChain.DigitalSupplyChainTracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateResponse {
    private String message;
    private ItemResponse item;
}
