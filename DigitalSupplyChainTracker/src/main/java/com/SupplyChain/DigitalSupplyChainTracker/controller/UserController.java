package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserLogin;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegister;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegister request) {

        //TODO: call service to register user

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserRegister(
                                "User registered successfully",
                                Role.SUPPLIER //TODO: replace with actual role
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLogin request) {

        return ResponseEntity.status(HttpStatus.OK).
                body(
                        new com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserLogin(
                                "jwt token here",
                                Role.SUPPLIER //TODO: replace with actual role
                        )
                );

    }

    // Admin can check all users
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        //todo return all users
        return ResponseEntity.status(HttpStatus.OK).body("All users");
    }


}
