package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.Utils.JwtUtils;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.LoginRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.LoginResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.exception.UserNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepo userRepo;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        //1. authenticate the user
       Authentication authContext =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));


        //2. Generate JWT token
        String jwtToken = jwtUtils.generateToken(authContext);

        UserEntity user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found with email: " + loginRequest.getEmail()));
        // Return the response(LoginResponse)
        return LoginResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }
}
