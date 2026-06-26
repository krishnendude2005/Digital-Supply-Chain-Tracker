package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ItemService {
    List<Item> getAllItems(Authentication authentication);
    Item addItem(AddItemRequest item);
    Item updateItem(Item item);
    void deleteItem(Long id);
    Item getItemById(Long id);

    List<Item> searchedItem(String category);
}
