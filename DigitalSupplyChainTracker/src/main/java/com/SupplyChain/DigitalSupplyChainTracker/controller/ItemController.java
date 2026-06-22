package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AddItem;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // Get all items
    @GetMapping("/items")
    public ResponseEntity<?> getItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    // Get item by ID
    @GetMapping("/item/{id}")
    public ResponseEntity<?> getItemById(Long id) {
       Item item = itemService.getItemById(id);
       return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    // Create an item
    @PostMapping("/item")
    public ResponseEntity<?> addItem(@RequestBody Item item) {

       Item savedItem = itemService.addItem(item);
       return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    // update an existing item through id
    @PutMapping("/item")
    public ResponseEntity<?> updateItem(@RequestBody Item item) {

        Item existingItem = itemService.getItemById(item.getId());

        existingItem.setName(item.getName());
        existingItem.setCategory(item.getCategory());
        existingItem.setSupplier(item.getSupplier());

        return ResponseEntity.status(HttpStatus.OK).body(
                new AddItem(existingItem.getName(), "item updated successfully")
        );
    }

    @DeleteMapping("/item")
    public ResponseEntity<?> deleteItem(@RequestParam Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("item deleted successfully");
    }

    // search items based on category
    @GetMapping("/item")
    public ResponseEntity<?> searchedItem(@RequestParam(name = "category") String category) {
        List<Item> items = itemService.searchedItem(category);
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

}
