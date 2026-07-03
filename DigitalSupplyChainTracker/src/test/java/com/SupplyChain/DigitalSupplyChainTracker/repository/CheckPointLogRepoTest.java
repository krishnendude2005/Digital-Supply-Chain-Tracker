package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CheckpointLogRepoTest {

    @Autowired
    private CheckpointLogRepo checkpointLogRepo;

    @Autowired
    private ShipmentRepo shipmentRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private UserRepo userRepo;

    @Test
    void findAllByShipment_ShipmentId_ValidShipmentId_ReturnsCheckpointLogs() {
        UserEntity supplier = saveSupplier("supplier@example.com");
        Item item = saveItem(supplier, "Test Item", "Electronics");

        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = saveShipment(item, shipmentId);

        CheckpointLog log1 = CheckpointLog.builder()
                .checkpointId(UUID.randomUUID())
                .location("Kolkata Hub")
                .itemStatus(ItemStatus.IN_TRANSIT)
                .shipment(shipment)
                .timestamp(LocalDateTime.now())
                .build();

        CheckpointLog log2 = CheckpointLog.builder()
                .checkpointId(UUID.randomUUID())
                .location("Delhi Hub")
                .itemStatus(ItemStatus.DELIVERED)
                .shipment(shipment)
                .timestamp(LocalDateTime.now())
                .build();

        checkpointLogRepo.saveAll(List.of(log1, log2));

        List<CheckpointLog> foundLogs = checkpointLogRepo.findAllByShipment_ShipmentId(shipmentId);

        assertEquals(2, foundLogs.size());
        assertTrue(foundLogs.stream()
                .allMatch(log -> log.getShipment().getShipmentId().equals(shipmentId)));
    }

    @Test
    void findAllByShipment_ShipmentId_InvalidShipmentId_ReturnsEmptyList() {
        UUID nonExistentShipmentId = UUID.randomUUID();

        List<CheckpointLog> foundLogs = checkpointLogRepo.findAllByShipment_ShipmentId(nonExistentShipmentId);

        assertNotNull(foundLogs);
        assertTrue(foundLogs.isEmpty());
    }

    private UserEntity saveSupplier(String email) {
        UserEntity supplier = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier")
                .email(email)
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        return userRepo.save(supplier);
    }

    private Item saveItem(UserEntity supplier, String name, String category) {
        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name(name)
                .category(category)
                .supplier(supplier)
                .build();

        return itemRepo.save(item);
    }

    private Shipment saveShipment(Item item, UUID shipmentId) {
        Shipment shipment = Shipment.builder()
                .shipmentId(shipmentId)
                .item(item)
                .fromLocation("Kolkata")
                .toLocation("Delhi")
                .shipmentStartDate(LocalDateTime.now())
                .shipmentExpectedDate(LocalDateTime.now().plusDays(2))
                .currentStatus(ShipmentStatus.CREATED)
                .build();

        return shipmentRepo.save(shipment);
    }
}