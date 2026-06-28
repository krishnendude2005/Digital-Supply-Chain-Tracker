package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ItemUpdateRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface ItemService {
    List<Item> getAllItems(Authentication authentication);
    Item addItem(AddItemRequest item);
    Item updateItem(ItemUpdateRequest updateRequest, UUID itemId);
    void deleteItemByItemId(UUID ItemId);
    Item getItemByItemId(UUID id);
    List<Item> searchedItem(String category);
}
