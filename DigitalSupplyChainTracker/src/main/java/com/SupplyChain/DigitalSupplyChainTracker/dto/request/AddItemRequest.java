package com.SupplyChain.DigitalSupplyChainTracker.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddItemRequest {
    private String name;
    private String category;
}
