package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.LoginRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegisterRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.LoginResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserRegisterResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.exception.UserNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserRegisterResponse register(UserRegisterRequest registerRequest) {

        //RegisterRequest -> UserEntity
        UserEntity newUser = convertToUserEntity(registerRequest);
        newUser.setUserId(UUID.randomUUID().toString());

        //save user
        newUser = userRepo.save(newUser);

        //return the newly saved user
        return convertEntityToUser(newUser);
    }


    // METHOD: converting registration request to user entity
    private UserEntity convertToUserEntity(UserRegisterRequest request) {
        return UserEntity.builder()
                .role(request.getRole())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    //METHOD: userEntity -> client response
    private UserRegisterResponse convertEntityToUser(UserEntity userEntity) {
        return UserRegisterResponse.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .build();
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserEntity changeRole(String role, Long id) {

        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setRole(Role.valueOf(role.toUpperCase()));

        return userRepo.save(user);
    }
}
