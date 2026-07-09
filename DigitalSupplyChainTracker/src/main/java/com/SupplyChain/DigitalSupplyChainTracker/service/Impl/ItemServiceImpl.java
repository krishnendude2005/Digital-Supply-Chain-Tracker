package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ItemUpdateRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ItemResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ResourceNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ShipmentAlreadyExistsException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ItemRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;
    private final UserRepo userRepo;
    private final ShipmentRepo shipmentRepo;

    @Override
    public List<ItemResponse> getAllItems(Authentication authentication) {

        //Authentication validation
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }

        String authority = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        assert authority != null;

        switch (authority) {
            case "ROLE_ADMIN":
                return itemRepo.findAll().stream()
                        .map(this::convertToItemResponse)
                        .toList();

            case "ROLE_SUPPLIER":
                String supplierEmail = authentication.getName();
                return itemRepo.findBySupplier_EmailIgnoreCase(supplierEmail).stream()
                        .map(this::convertToItemResponse)
                        .toList();

            default:
                return List.of();
        }
    }

    @Override
    public ItemResponse addItem(AddItemRequest item) {

        //Get the supplier from DB. Using the Currently logged-in user
        String currentLoggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        assert currentLoggedInUserEmail != null;

        UserEntity currentLoggedInUser = userRepo.findByEmail(currentLoggedInUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentLoggedInUserEmail));

        Item itemToSave = Item.builder()
                .name(item.getName())
                .itemId(UUID.randomUUID())
                .category(item.getCategory())
                .supplier(currentLoggedInUser)
                .build();

        Item savedItem = itemRepo.save(itemToSave);
        return convertToItemResponse(savedItem);
    }

    @Override
    public ItemResponse updateItem(ItemUpdateRequest updateRequest, UUID itemId) {
        Item itemToUpdate = itemRepo.findByItemId(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        UserEntity updatedSupplier = userRepo.findByEmail(updateRequest.getSupplierEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with email: " + updateRequest.getSupplierEmail()));

        itemToUpdate.setName(updateRequest.getName());
        itemToUpdate.setCategory(updateRequest.getCategory());
        itemToUpdate.setSupplier(updatedSupplier);

        Item updatedItem = itemRepo.save(itemToUpdate);
        return convertToItemResponse(updatedItem);
    }

    @Override
    public void deleteItemByItemId(UUID itemId) {

        if (!itemRepo.existsByItemId(itemId)) {
            throw new ResourceNotFoundException("Item not found with id: " + itemId);
        }

        // Performs a soft delete on the item,
        // but only after validating that it is not currently linked to any active shipments(CREATED, IN_TRANSIT)
        if(shipmentRepo.existsByItem_ItemId(itemId)) {
            throw new ShipmentAlreadyExistsException("Delete Item Not Possible \n" + "Shipment already exists for this Item with ItemID:" + itemId );
        }
        itemRepo.deleteByItemId(itemId);
    }

    @Override
    public ItemResponse getItemByItemId(UUID itemId) {

        //Admin can view all items

        //Give result according to the role of the user
        String currentLoggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Item item;
        if(userRepo.findByEmail(currentLoggedInUserEmail).get().getRole() == Role.ADMIN) {
            item = itemRepo.findByItemId(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        } else {
            item = itemRepo.findBySupplier_EmailIgnoreCaseAndItemId(currentLoggedInUserEmail, itemId).orElseThrow(() ->
                    new ResourceNotFoundException("Item not found with id: " + itemId));
        }
        return convertToItemResponse(item);
    }

    @Override
    public List<ItemResponse> searchedItem(String category) {
        String currentLoggedInUserEmail =
                SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity currentLoggedInUser = userRepo.findByEmail(currentLoggedInUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + currentLoggedInUserEmail));

        switch (currentLoggedInUser.getRole()) {
            case ADMIN:
                return itemRepo.findByCategoryIgnoreCase(category).stream()
                        .map(this::convertToItemResponse)
                        .toList();

            case SUPPLIER:
                return itemRepo.findByCategoryIgnoreCaseAndSupplier_EmailIgnoreCase(
                        category, currentLoggedInUserEmail).stream()
                        .map(this::convertToItemResponse)
                        .toList();

            default:
                return Collections.emptyList();
        }
    }

    private ItemResponse convertToItemResponse(Item item) {
        return ItemResponse.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .category(item.getCategory())
                .supplierEmail(item.getSupplier() != null ? item.getSupplier().getEmail() : null)
                .supplierName(item.getSupplier() != null ? item.getSupplier().getName() : null)
                .supplierRole(item.getSupplier() != null ? item.getSupplier().getRole() : null)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
