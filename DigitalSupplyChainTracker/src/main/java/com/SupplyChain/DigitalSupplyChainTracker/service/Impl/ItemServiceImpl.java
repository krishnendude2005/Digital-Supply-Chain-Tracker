package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ResourceNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ItemRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;
    private final UserRepo userRepo;

    @Override
    public List<Item> getAllItems(Authentication authentication) {

        //Authentication validation
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }

        //Extract the user's role and email from the authentication object
//        List<String> roles = authentication.getAuthorities()
//                .stream()
//                .map(item -> item.getAuthority())
//                .toList();

        // Based on the user's role, fetch items from the appropriate repository
//        if (roles.contains(("ROLE_" + Role.ADMIN.name()))) {
//            return itemRepo.findAll();
//        }
//
//        if (roles.contains("ROLE_" + Role.SUPPLIER.name())) {
//            String supplier = authentication.getName();
//            return itemRepo.findBySupplier(supplier);
//        }

        String authority = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        assert authority != null;

        switch (authority) {
            case "ROLE_ADMIN":
                return itemRepo.findAll();

            case "ROLE_SUPPLIER":
                String supplierEmail = authentication.getName();
                return itemRepo.findBySupplier(supplierEmail);


            default:
                return List.of();

        }


    }

    @Override
    public Item addItem(AddItemRequest item) {

        //Get the supplier from DB. Using the Currently logged-in user
        String currentLoggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        assert currentLoggedInUserEmail != null;

        UserEntity currentLoggedInUser = userRepo.findByEmail(currentLoggedInUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentLoggedInUserEmail));

        Item itemToSave = Item.builder()
                .name(item.getName())
                .itemId(UUID.randomUUID().toString())
                .category(item.getCategory())
                .createdDate(LocalDateTime.now())
                .supplier(currentLoggedInUser)
                .build();

        return itemRepo.save(itemToSave);

    }

    @Override
    public Item updateItem(Item item) {
        return itemRepo.save(item);
    }

    @Override
    public void deleteItem(Long id) {

        if (!itemRepo.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }

        itemRepo.deleteById(id);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Item not found with id: " + id)
        );
    }

    @Override
    public List<Item> searchedItem(String category) {
        return itemRepo.findByCategory(category);
    }
}
