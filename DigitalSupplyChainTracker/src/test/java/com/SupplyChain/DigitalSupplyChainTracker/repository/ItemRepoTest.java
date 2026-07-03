package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
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
class ItemRepoTest {

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private UserRepo userRepo;

    @Test
    void findByItemId_ValidId_ReturnsItem() {
        UserEntity supplier = saveSupplier("supplier@example.com");

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();

        Item savedItem = itemRepo.save(item);

        UUID itemId = savedItem.getItemId();
        Optional<Item> foundItem = itemRepo.findByItemId(itemId);

        assertTrue(foundItem.isPresent());
        assertEquals(savedItem.getId(), foundItem.get().getId());
        assertEquals(itemId, foundItem.get().getItemId());
        assertEquals("Test Item", foundItem.get().getName());
    }

    @Test
    void findByItemId_InvalidId_ReturnsEmpty() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Item> foundItem = itemRepo.findByItemId(nonExistentId);

        assertFalse(foundItem.isPresent());
    }

    @Test
    void findBySupplier_EmailIgnoreCase_ReturnsItems() {
        UserEntity supplier = saveSupplier("supplier@example.com");

        Item item1 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 1")
                .category("Electronics")
                .supplier(supplier)
                .build();

        Item item2 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 2")
                .category("Furniture")
                .supplier(supplier)
                .build();

        itemRepo.saveAll(List.of(item1, item2));

        List<Item> foundItems = itemRepo.findBySupplier_EmailIgnoreCase("supplier@example.com");

        assertEquals(2, foundItems.size());
        assertTrue(foundItems.stream()
                .allMatch(item -> item.getSupplier().getEmail().equals("supplier@example.com")));
    }

    @Test
    void findByCategoryIgnoreCase_ReturnsItems() {
        UserEntity supplier = saveSupplier("supplier@example.com");

        Item item1 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 1")
                .category("Electronics")
                .supplier(supplier)
                .build();

        Item item2 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 2")
                .category("electronics")
                .supplier(supplier)
                .build();

        itemRepo.saveAll(List.of(item1, item2));

        List<Item> foundItems = itemRepo.findByCategoryIgnoreCase("Electronics");

        assertEquals(2, foundItems.size());
        assertTrue(foundItems.stream()
                .allMatch(item -> item.getCategory().equalsIgnoreCase("Electronics")));
    }

    @Test
    void findByCategoryIgnoreCaseAndSupplier_EmailIgnoreCase_ReturnsItems() {
        UserEntity supplier1 = saveSupplier("supplier1@example.com");
        UserEntity supplier2 = saveSupplier("supplier2@example.com");

        Item item1 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 1")
                .category("Electronics")
                .supplier(supplier1)
                .build();

        Item item2 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 2")
                .category("Electronics")
                .supplier(supplier2)
                .build();

        itemRepo.saveAll(List.of(item1, item2));

        List<Item> foundItems = itemRepo.findByCategoryIgnoreCaseAndSupplier_EmailIgnoreCase(
                "Electronics",
                "supplier1@example.com"
        );

        assertEquals(1, foundItems.size());
        assertEquals("Item 1", foundItems.get(0).getName());
    }

    @Test
    void existsByItemId_ReturnsTrueForExistingItem() {
        UserEntity supplier = saveSupplier("supplier@example.com");

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();

        Item savedItem = itemRepo.save(item);

        boolean exists = itemRepo.existsByItemId(savedItem.getItemId());

        assertTrue(exists);
    }

    @Test
    void existsByItemId_ReturnsFalseForNonExistingItem() {
        UUID nonExistentId = UUID.randomUUID();

        boolean exists = itemRepo.existsByItemId(nonExistentId);

        assertFalse(exists);
    }

    @Test
    void findAll_ReturnsAllItems() {
        UserEntity supplier1 = saveSupplier("supplier1@example.com");
        UserEntity supplier2 = saveSupplier("supplier2@example.com");

        Item item1 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 1")
                .category("Electronics")
                .supplier(supplier1)
                .build();

        Item item2 = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Item 2")
                .category("Furniture")
                .supplier(supplier2)
                .build();

        itemRepo.saveAll(List.of(item1, item2));

        List<Item> allItems = itemRepo.findAll();

        assertEquals(2, allItems.size());
    }

    @Test
    void deleteByItemId_RemovesItem() {
        UserEntity supplier = saveSupplier("supplier@example.com");

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();

        Item savedItem = itemRepo.save(item);
        UUID itemId = savedItem.getItemId();

        itemRepo.deleteByItemId(itemId);

        Optional<Item> deletedItem = itemRepo.findByItemId(itemId);

        assertFalse(deletedItem.isPresent());
    }

    @Test
    void save_ValidItem_SavesSuccessfully() {
        UserEntity supplier = saveSupplier("supplier@example.com");

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("New Item")
                .category("New Category")
                .supplier(supplier)
                .build();

        Item savedItem = itemRepo.save(item);

        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals("New Item", savedItem.getName());
        assertEquals("New Category", savedItem.getCategory());
        assertEquals(supplier.getUserId(), savedItem.getSupplier().getUserId());
    }

    private UserEntity saveSupplier(String email) {
        UserEntity supplier = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier")
                .email(email)
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        return userRepo.save(supplier);
    }
}