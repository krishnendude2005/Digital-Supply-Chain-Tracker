package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegisterRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@EnableMethodSecurity
public class UserController {

    private final UserServiceImpl userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {

        userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");

    }


    // Admin can check all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserEntity> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    //Change Role
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{id}/role")
    public ResponseEntity<?> changeRole(String role, @PathVariable Long id) {
        UserEntity changedRoleUser = userService.changeRole(role, id);

        return ResponseEntity.status(HttpStatus.OK).body(changedRoleUser);
    }




}
