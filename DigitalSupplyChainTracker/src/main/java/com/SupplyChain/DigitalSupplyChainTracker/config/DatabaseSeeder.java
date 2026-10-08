package com.SupplyChain.DigitalSupplyChainTracker.config;

import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        //If no admin account exists, create one(Default)
        if(userRepo.findByRole(Role.ADMIN).isEmpty()) {
            UserEntity systemAdmin = UserEntity
                    .builder()
                    .name("System Admin")
                    .userId(UUID.randomUUID())
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.ADMIN)
                    .build();

            userRepo.save(systemAdmin);
            log.info("Initial Admin Account Created Successfully");
        }

        if(userRepo.findByRole(Role.SUPPLIER).isEmpty()) {
            UserEntity supplier = UserEntity
                    .builder()
                    .name("Supplier One")
                    .userId(UUID.randomUUID())
                    .email("supplier1@example.com")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.SUPPLIER)
                    .build();
            userRepo.save(supplier);
            log.info("Initial Supplier Account Created Successfully");
        }

        if(userRepo.findByRole(Role.TRANSPORTER).isEmpty()) {
            UserEntity transporter = UserEntity
                    .builder()
                    .name("Transporter One")
                    .userId(UUID.randomUUID())
                    .email("transporter1@example.com")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.TRANSPORTER)
                    .build();
            userRepo.save(transporter);
            log.info("Initial Transporter Account Created Successfully");
        }
    }
}
