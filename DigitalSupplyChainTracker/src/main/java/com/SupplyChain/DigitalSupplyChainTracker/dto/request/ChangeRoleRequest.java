package com.SupplyChain.DigitalSupplyChainTracker.dto.request;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRoleRequest {
    private String email;
    private Role role;
}
