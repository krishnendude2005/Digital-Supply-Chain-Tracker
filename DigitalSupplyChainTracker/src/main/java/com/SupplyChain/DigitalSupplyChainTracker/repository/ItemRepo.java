package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepo extends JpaRepository<Item, Long> {
  List<Item> findByCategory(String category);
  List<Item> findBySupplier(String supplier);
}

