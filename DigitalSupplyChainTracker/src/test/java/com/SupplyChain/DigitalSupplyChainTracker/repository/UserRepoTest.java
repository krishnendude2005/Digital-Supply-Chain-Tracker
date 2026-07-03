package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    @Test
    void findByEmail_ValidEmail_ReturnsUser() {
        UserEntity user = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        UserEntity savedUser = userRepo.save(user);

        Optional<UserEntity> foundUser = userRepo.findByEmail("john@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("john@example.com", foundUser.get().getEmail());
        assertEquals(Role.SUPPLIER, foundUser.get().getRole());
    }

    @Test
    void findByEmail_InvalidEmail_ReturnsEmpty() {
        Optional<UserEntity> foundUser = userRepo.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByRole_ReturnsUsersWithGivenRole() {
        UserEntity supplier1 = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier 1")
                .email("supplier1@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        UserEntity supplier2 = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier 2")
                .email("supplier2@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        UserEntity admin = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Admin")
                .email("admin@example.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        userRepo.saveAll(List.of(supplier1, supplier2, admin));

        List<UserEntity> suppliers = userRepo.findByRole(Role.SUPPLIER);

        assertEquals(2, suppliers.size());
        assertTrue(suppliers.stream().allMatch(user -> user.getRole() == Role.SUPPLIER));
    }

    @Test
    void findByUserId_ValidId_ReturnsUser() {
        UserEntity user = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        UUID userId = UUID.randomUUID();
        user.setUserId(userId);

        UserEntity savedUser = userRepo.save(user);

        Optional<UserEntity> foundUser = userRepo.findByUserId(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(userId, foundUser.get().getUserId());
    }

    @Test
    void save_ValidUser_SavesSuccessfully() {
        UserEntity user = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Jane Doe")
                .email("jane@example.com")
                .password("encodedPassword")
                .role(Role.WAREHOUSE_MANAGER)
                .build();

        UserEntity savedUser = userRepo.save(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("Jane Doe", savedUser.getName());
        assertEquals("jane@example.com", savedUser.getEmail());
        assertEquals(Role.WAREHOUSE_MANAGER, savedUser.getRole());
    }

    @Test
    void findAll_ReturnsAllUsers() {
        UserEntity user1 = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("User 1")
                .email("user1@example.com")
                .password("encodedPassword")
                .role(Role.TRANSPORTER)
                .build();

        UserEntity user2 = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("User 2")
                .email("user2@example.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        userRepo.saveAll(List.of(user1, user2));

        List<UserEntity> users = userRepo.findAll();

        assertEquals(2, users.size());
    }
}