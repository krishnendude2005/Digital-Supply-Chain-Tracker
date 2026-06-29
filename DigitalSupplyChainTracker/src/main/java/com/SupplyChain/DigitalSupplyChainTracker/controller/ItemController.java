package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ItemUpdateRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AddItem;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ItemUpdateResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // Get all items
    @GetMapping
    public ResponseEntity<?> getItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Item> items = itemService.getAllItems(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    // Get item by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(UUID id) {
       Item item = itemService.getItemByItemId(id);
       return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    // Create an item (Only for SUPPLIER and ADMIN) todo: Need to decide if ADMIN can create item
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLIER')")
    public ResponseEntity<?> addItem(@RequestBody AddItemRequest item) {

       Item savedItem = itemService.addItem(item);
       return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    // update an existing item through ItemId
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@RequestBody ItemUpdateRequest updateItemReq, @PathVariable UUID itemId) {

        Item updatedItem = itemService.updateItem(updateItemReq, itemId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ItemUpdateResponse("item", updatedItem)
        );
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN', 'SUPPLIER')")
    public ResponseEntity<?> deleteItem(@RequestParam UUID itemId) {
        itemService.deleteItemByItemId(itemId);
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

    //Get a particular item metrics
    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemMetrics(@PathVariable UUID itemId) {
        Item item = itemService.getItemByItemId(itemId);
        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

}
