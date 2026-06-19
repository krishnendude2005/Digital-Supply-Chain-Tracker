package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AddItem;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    // Get all items
    @GetMapping("/items")
    public ResponseEntity<?> getItems() {
        List<Item> items = new ArrayList<>();
        //TODO: call service method to get all the items

        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    // Create an item
    @PostMapping("/item")
    public ResponseEntity<?> addItem(@RequestBody Item item) {

        //todo: call service method to add the item and it will return the item
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AddItem(item.getName(), "item created successfully")
        );
    }

    // update an existing item through id
    @PutMapping("/item")
    public ResponseEntity<?> updateItem(@RequestBody Item item) {

        Item existingItem = new Item(); //TODO: call service method to get the item by id

        existingItem.setName(item.getName());
        existingItem.setCategory(item.getCategory());
        existingItem.setSupplier(item.getSupplier());

        return ResponseEntity.status(HttpStatus.OK).body(
                new AddItem(existingItem.getName(), "item updated successfully")
        );
    }

    @DeleteMapping("/item")
    public ResponseEntity<?> deleteItem(@RequestParam int itemId) {
        //todo: call service method to delete the item by id
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("item deleted successfully");
    }

}
