package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAllItems();
    Item addItem(Item item);
    Item updateItem(Item item);
    void deleteItem(Long id);

}
