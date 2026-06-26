package com.SupplyChain.DigitalSupplyChainTracker.config;

import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                    .userId(UUID.randomUUID().toString())
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.ADMIN)
                    .build();

            userRepo.save(systemAdmin);

            System.out.println("✅ Initial Admin Account Created Successfully!");
        }
    }
}
