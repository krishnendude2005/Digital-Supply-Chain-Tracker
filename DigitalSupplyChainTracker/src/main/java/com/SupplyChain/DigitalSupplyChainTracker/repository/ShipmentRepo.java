package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByShipmentId(UUID shipmentId);
    List<Shipment> findByItem_Supplier_EmailIgnoreCase(String supplierEmail);
    List<Shipment> findByAssignedTransporter_EmailIgnoreCase(String transporterEmail);

    //Method for finding shipments based on 1 condition
    List<Shipment> findByShipmentExpectedDateBeforeAndCurrentStatusNot(
            LocalDateTime expectedDate,
            ShipmentStatus currentStatus
    );

    //Method go get Shipment based on their status
    List<Shipment> findByShipmentExpectedDateBeforeAndCurrentStatus(
            LocalDateTime expectedDate,
            ShipmentStatus currentStatus
    );

    boolean existsByItem_ItemId(UUID itemId);
}
