package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepo extends JpaRepository<Item, Long> {
    List<Item> findByCategory(String category);

    List<Item> findBySupplier(String supplier);

    Optional<Item> findByItemId(UUID uuid);

    Boolean existsByItemId(UUID uuid);

    void deleteByItemId(UUID uuid);

    //
    List<Item> findByCategoryIgnoreCase(String category);

    List<Item> findBySupplierAndCategoryIgnoreCase(String supplier, String category);
    //
    Optional<Item> findByItemIdAndSupplierIgnoreCase(UUID uuid, String supplier);
}

