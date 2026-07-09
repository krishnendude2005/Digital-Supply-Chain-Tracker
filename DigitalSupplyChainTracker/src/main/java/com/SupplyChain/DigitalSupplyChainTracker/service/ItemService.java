package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ItemUpdateRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ItemResponse;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface ItemService {
    List<ItemResponse> getAllItems(Authentication authentication);
    ItemResponse addItem(AddItemRequest item);
    ItemResponse updateItem(ItemUpdateRequest updateRequest, UUID itemId);
    void deleteItemByItemId(UUID ItemId);
    ItemResponse getItemByItemId(UUID id);
    List<ItemResponse> searchedItem(String category);
}
