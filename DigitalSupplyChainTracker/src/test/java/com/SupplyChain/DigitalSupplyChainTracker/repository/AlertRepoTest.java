package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
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
class AlertRepoTest {

    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private CheckpointLogRepo checkpointLogRepo;

    @Autowired
    private ShipmentRepo shipmentRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private UserRepo userRepo;

    @Test
    void findAllByCheckpointLog_Shipment_ShipmentId_ValidShipmentId_ReturnsAlerts() {
        UserEntity supplier = saveSupplier("supplier@example.com");
        Item item = saveItem(supplier, "Laptop", "Electronics");

        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = saveShipment(item, shipmentId);

        CheckpointLog checkpointLog = saveCheckpointLog(shipment, "Kolkata Hub", ItemStatus.IN_TRANSIT);

        Alert alert1 = new Alert();
        alert1.setAlertId(UUID.randomUUID());
        alert1.setMessage("Shipment delayed at Kolkata Hub");
        alert1.setCheckpointLog(checkpointLog);
        alert1.setCreatedAt(LocalDateTime.now());


        Alert alert2 = new Alert();
        alert2.setAlertId(UUID.randomUUID());
        alert2.setMessage("Temperature issue detected");
        alert2.setCheckpointLog(checkpointLog);
        alert2.setCreatedAt(LocalDateTime.now());

        alertRepo.saveAll(List.of(alert1, alert2));

        List<Alert> foundAlerts = alertRepo.findAllByCheckpointLog_Shipment_ShipmentId(shipmentId);

        assertEquals(2, foundAlerts.size());
        assertTrue(foundAlerts.stream()
                .allMatch(alert -> alert.getCheckpointLog().getShipment().getShipmentId().equals(shipmentId)));
    }

    @Test
    void findAllByCheckpointLog_Shipment_ShipmentId_InvalidShipmentId_ReturnsEmptyList() {
        UUID nonExistentShipmentId = UUID.randomUUID();

        List<Alert> foundAlerts = alertRepo.findAllByCheckpointLog_Shipment_ShipmentId(nonExistentShipmentId);

        assertNotNull(foundAlerts);
        assertTrue(foundAlerts.isEmpty());
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

    private CheckpointLog saveCheckpointLog(Shipment shipment, String location, ItemStatus itemStatus) {
        CheckpointLog checkpointLog = CheckpointLog.builder()
                .checkpointId(UUID.randomUUID())
                .location(location)
                .itemStatus(itemStatus)
                .shipment(shipment)
                .timestamp(LocalDateTime.now())
                .build();

        return checkpointLogRepo.save(checkpointLog);
    }
}