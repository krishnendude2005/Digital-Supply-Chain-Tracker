package com.SupplyChain.DigitalSupplyChainTracker.service;


import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ChangeRoleRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegisterRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserRegisterResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserRegisterResponse register(UserRegisterRequest registerRequest);
    List<UserResponse> getAllUsers();
    UserResponse changeRole(ChangeRoleRequest newRole);
}
