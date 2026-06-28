package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ChangeRoleRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegisterRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.UserServiceImpl;
import com.SupplyChain.DigitalSupplyChainTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
        List<UserEntity> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    //Change Role
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/role")
    public ResponseEntity<?> changeRole(@RequestBody ChangeRoleRequest newRole) {
        UserEntity changedRoleUser = userService.changeRole(newRole);

        return ResponseEntity.status(HttpStatus.OK).body(changedRoleUser);
    }




}
