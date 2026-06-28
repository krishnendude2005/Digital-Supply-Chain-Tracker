package com.SupplyChain.DigitalSupplyChainTracker.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ItemUpdateRequest {
    private String name;
    private String category;
    private String supplierEmail;
}
