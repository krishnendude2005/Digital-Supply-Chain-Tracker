package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ChangeRoleRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegisterRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserResponse;
import com.SupplyChain.DigitalSupplyChainTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {

        userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");

    }


    // Admin can check all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    //Change Role
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/role")
    public ResponseEntity<?> changeRole(@RequestBody ChangeRoleRequest newRole) {
        UserResponse changedRoleUser = userService.changeRole(newRole);

        return ResponseEntity.status(HttpStatus.OK).body(changedRoleUser);
    }




}
