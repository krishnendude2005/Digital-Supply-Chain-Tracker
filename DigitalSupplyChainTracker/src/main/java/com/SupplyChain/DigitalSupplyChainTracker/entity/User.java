package com.SupplyChain.DigitalSupplyChainTracker.entity;

import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private Role role;
}
