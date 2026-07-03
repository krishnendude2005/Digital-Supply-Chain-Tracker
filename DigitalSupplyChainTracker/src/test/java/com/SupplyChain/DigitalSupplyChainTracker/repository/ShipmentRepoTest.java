package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ShipmentRepoTest {

    @Autowired
    private ShipmentRepo shipmentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Test
    void findByShipmentId_ValidId_ReturnsShipment() {
        UserEntity supplier = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier")
                .email("supplier@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();
        supplier = userRepo.save(supplier);

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();
        item = itemRepo.save(item);

        Shipment shipment = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(ShipmentStatus.CREATED)
                .assignedTransporter(null)
                .build();
        Shipment savedShipment = shipmentRepo.save(shipment);

        UUID shipmentId = savedShipment.getShipmentId();
        Optional<Shipment> foundShipment = shipmentRepo.findByShipmentId(shipmentId);

        assertTrue(foundShipment.isPresent());
        assertEquals(savedShipment.getId(), foundShipment.get().getId());
        assertEquals(shipmentId, foundShipment.get().getShipmentId());
        assertEquals(ShipmentStatus.CREATED, foundShipment.get().getCurrentStatus());
    }

    @Test
    void findByShipmentId_InvalidId_ReturnsEmpty() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Shipment> foundShipment = shipmentRepo.findByShipmentId(nonExistentId);
        assertFalse(foundShipment.isPresent());
    }

    @Test
    void findByItem_Supplier_EmailIgnoreCase_ReturnsShipments() {
        UserEntity supplier = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier")
                .email("supplier@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();
        supplier = userRepo.save(supplier);

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();
        item = itemRepo.save(item);

        Shipment shipment1 = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(ShipmentStatus.CREATED)
                .assignedTransporter(null)
                .build();
        Shipment shipment2 = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("Warehouse C")
                .toLocation("Warehouse D")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(5))
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .assignedTransporter(null)
                .build();

        shipmentRepo.saveAll(List.of(shipment1, shipment2));

        List<Shipment> foundShipments = shipmentRepo.findByItem_Supplier_EmailIgnoreCase("supplier@example.com");
        assertEquals(2, foundShipments.size());
        assertTrue(foundShipments.stream().allMatch(shipment -> shipment.getItem().getSupplier().getEmail().equals("supplier@example.com")));
    }

    @Test
    void findByAssignedTransporter_EmailIgnoreCase_ReturnsShipments() {
        UserEntity transporter = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Transporter")
                .email("transporter@example.com")
                .password("encodedPassword")
                .role(Role.TRANSPORTER)
                .build();
        transporter = userRepo.save(transporter);

        UserEntity supplier = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier")
                .email("supplier@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();
        supplier = userRepo.save(supplier);

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();
        item = itemRepo.save(item);

        Shipment shipment1 = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(ShipmentStatus.CREATED)
                .assignedTransporter(transporter)
                .build();
        Shipment shipment2 = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("Warehouse C")
                .toLocation("Warehouse D")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(5))
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .assignedTransporter(null)
                .build();

        shipmentRepo.saveAll(List.of(shipment1, shipment2));

        List<Shipment> foundShipments = shipmentRepo.findByAssignedTransporter_EmailIgnoreCase("transporter@example.com");
        assertEquals(1, foundShipments.size());
        assertEquals(transporter.getEmail(), foundShipments.get(0).getAssignedTransporter().getEmail());
    }

    @Test
    void findAll_ReturnsAllShipments() {
        UserEntity supplier = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier")
                .email("supplier@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();
        supplier = userRepo.save(supplier);

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();
        item = itemRepo.save(item);

        Shipment shipment1 = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(ShipmentStatus.CREATED)
                .assignedTransporter(null)
                .build();
        Shipment shipment2 = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("Warehouse C")
                .toLocation("Warehouse D")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(5))
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .assignedTransporter(null)
                .build();

        shipmentRepo.saveAll(List.of(shipment1, shipment2));

        List<Shipment> allShipments = shipmentRepo.findAll();
        assertEquals(2, allShipments.size());
    }

    @Test
    void save_ValidShipment_SavesSuccessfully() {
        UserEntity supplier = UserEntity.builder()
                .userId(UUID.randomUUID())
                .name("Supplier")
                .email("supplier@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();
        supplier = userRepo.save(supplier);

        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .name("Test Item")
                .category("Electronics")
                .supplier(supplier)
                .build();
        item = itemRepo.save(item);

        Shipment shipment = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation("New Warehouse")
                .toLocation("New Destination")
                .shipmentExpectedDate(LocalDateTime.now().plusDays(10))
                .currentStatus(ShipmentStatus.CREATED)
                .assignedTransporter(null)
                .build();

        Shipment savedShipment = shipmentRepo.save(shipment);

        assertNotNull(savedShipment);
        assertNotNull(savedShipment.getId());
        assertEquals("New Warehouse", savedShipment.getFromLocation());
        assertEquals("New Destination", savedShipment.getToLocation());
        assertEquals(ShipmentStatus.CREATED, savedShipment.getCurrentStatus());
    }
}