package com.SupplyChain.DigitalSupplyChainTracker.service;


import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ChangeRoleRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.LoginRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegisterRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.LoginResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserRegisterResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserRegisterResponse register(UserRegisterRequest registerRequest);
    List<UserEntity> getAllUsers();
    UserEntity changeRole(ChangeRoleRequest newRole);
}
