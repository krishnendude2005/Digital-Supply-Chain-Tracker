package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
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

    @Test
    void findAllByType_ReturnsOnlyMatchingAlerts() {
        UserEntity supplier = saveSupplier("supplier2@example.com");
        Item item = saveItem(supplier, "Phone", "Electronics");
        Shipment shipment = saveShipment(item, UUID.randomUUID());
        CheckpointLog checkpointLog = saveCheckpointLog(shipment, "Delhi Hub", ItemStatus.IN_TRANSIT);

        saveAlert(checkpointLog, AlertType.DELAYED, false);
        saveAlert(checkpointLog, AlertType.DAMAGED, false);
        saveAlert(checkpointLog, AlertType.DELAYED, false);

        List<Alert> delayedAlerts = alertRepo.findAllByType(AlertType.DELAYED);
        List<Alert> damagedAlerts = alertRepo.findAllByType(AlertType.DAMAGED);

        assertEquals(2, delayedAlerts.size());
        assertTrue(delayedAlerts.stream().allMatch(alert -> alert.getType() == AlertType.DELAYED));
        assertEquals(1, damagedAlerts.size());
        assertEquals(AlertType.DAMAGED, damagedAlerts.get(0).getType());
    }

    @Test
    void findAllByType_NoMatch_ReturnsEmptyList() {
        List<Alert> alerts = alertRepo.findAllByType(AlertType.DAMAGED);

        assertNotNull(alerts);
        assertTrue(alerts.isEmpty());
    }

    @Test
    void existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse_UnresolvedAlert_ReturnsTrue() {
        UserEntity supplier = saveSupplier("supplier3@example.com");
        Item item = saveItem(supplier, "Tablet", "Electronics");
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = saveShipment(item, shipmentId);
        CheckpointLog checkpointLog = saveCheckpointLog(shipment, "Mumbai Hub", ItemStatus.IN_TRANSIT);

        saveAlert(checkpointLog, AlertType.DELAYED, false);

        boolean exists = alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(
                shipmentId, AlertType.DELAYED);

        assertTrue(exists);
    }

    @Test
    void existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse_ResolvedAlert_ReturnsFalse() {
        UserEntity supplier = saveSupplier("supplier4@example.com");
        Item item = saveItem(supplier, "Monitor", "Electronics");
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = saveShipment(item, shipmentId);
        CheckpointLog checkpointLog = saveCheckpointLog(shipment, "Chennai Hub", ItemStatus.IN_TRANSIT);

        saveAlert(checkpointLog, AlertType.DELAYED, true);

        boolean exists = alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(
                shipmentId, AlertType.DELAYED);

        assertFalse(exists);
    }

    @Test
    void existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse_DifferentType_ReturnsFalse() {
        UserEntity supplier = saveSupplier("supplier5@example.com");
        Item item = saveItem(supplier, "Keyboard", "Electronics");
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = saveShipment(item, shipmentId);
        CheckpointLog checkpointLog = saveCheckpointLog(shipment, "Pune Hub", ItemStatus.IN_TRANSIT);

        saveAlert(checkpointLog, AlertType.DAMAGED, false);

        boolean exists = alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(
                shipmentId, AlertType.DELAYED);

        assertFalse(exists);
    }

    private Alert saveAlert(CheckpointLog checkpointLog, AlertType type, boolean resolved) {
        Alert alert = Alert.builder()
                .alertId(UUID.randomUUID())
                .message(type + " alert")
                .type(type)
                .resolved(resolved)
                .checkpointLog(checkpointLog)
                .createdAt(LocalDateTime.now())
                .build();

        return alertRepo.save(alert);
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