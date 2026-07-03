package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ChangeRoleRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.UserRegisterRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.UserRegisterResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.exception.UserNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegisterRequest registerRequest;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        registerRequest = new UserRegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.SUPPLIER);

        userEntity = UserEntity.builder()
                .id(1L)
                .userId(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .password("$2a$10$encodedPassword")
                .role(Role.SUPPLIER)
                .build();
    }

    @Test
    void register_Success() {
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("$2a$10$encodedPassword");
        when(userRepo.save(any(UserEntity.class))).thenReturn(userEntity);

        UserRegisterResponse result = userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(Role.SUPPLIER, result.getRole());

        verify(passwordEncoder).encode("password123");
        verify(userRepo).save(any(UserEntity.class));
    }

    @Test
    void getAllUsers_Success() {
        List<UserEntity> users = List.of(userEntity);
        when(userRepo.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("john@example.com", result.get(0).getEmail());

        verify(userRepo).findAll();
    }

    @Test
    void changeRole_Success() {
        ChangeRoleRequest roleRequest = new ChangeRoleRequest();
        roleRequest.setEmail("john@example.com");
        roleRequest.setRole(Role.ADMIN);

        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(userEntity));
        when(userRepo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userService.changeRole(roleRequest);

        assertNotNull(result);
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals("john@example.com", result.getEmail());

        verify(userRepo).findByEmail("john@example.com");
        verify(userRepo).save(any(UserEntity.class));
    }

    @Test
    void changeRole_UserNotFound() {
        ChangeRoleRequest roleRequest = new ChangeRoleRequest();
        roleRequest.setEmail("nonexistent@example.com");
        roleRequest.setRole(Role.ADMIN);

        when(userRepo.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.changeRole(roleRequest));
        verify(userRepo, never()).save(any());
    }


}