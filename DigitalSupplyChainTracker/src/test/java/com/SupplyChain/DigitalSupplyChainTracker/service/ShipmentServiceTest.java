
package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.TransporterToAssignRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ShipmentResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ShipmentStatusChangeResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ResourceNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.exception.UserNotMatch;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ItemRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.ShipmentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ItemRepo itemRepo;

    @Mock
    private ShipmentRepo shipmentRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    private UserEntity supplierUser;
    private UserEntity adminUser;
    private UserEntity transporterUser;
    private Item item;
    private Shipment shipment;
    private ShipmentRequest shipmentRequest;
    private TransporterToAssignRequest transporterRequest;
    private UUID itemId;
    private UUID shipmentId;
    private UUID transporterId;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        shipmentId = UUID.randomUUID();
        transporterId = UUID.randomUUID();

        supplierUser = UserEntity.builder()
                .id(1L)
                .userId(UUID.randomUUID())
                .name("Supplier User")
                .email("supplier1@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        adminUser = UserEntity.builder()
                .id(2L)
                .userId(UUID.randomUUID())
                .name("Admin User")
                .email("admin@gmail.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        transporterUser = UserEntity.builder()
                .id(3L)
                .userId(transporterId)
                .name("Transporter User")
                .email("transporter1@example.com")
                .password("encodedPassword")
                .role(Role.TRANSPORTER)
                .build();

        item = Item.builder()
                .id(1L)
                .itemId(itemId)
                .name("Test Item")
                .category("Electronics")
                .supplier(supplierUser)
                .build();

        shipment = Shipment.builder()
                .id(1L)
                .shipmentId(shipmentId)
                .item(item)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .shipmentStartDate(null)
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(ShipmentStatus.CREATED)
                .assignedTransporter(null)
                .build();

        shipmentRequest = new ShipmentRequest();
        shipmentRequest.setItemId(itemId);
        shipmentRequest.setFromLocation("Warehouse A");
        shipmentRequest.setToLocation("Warehouse B");
        shipmentRequest.setEndDate(LocalDateTime.now().plusDays(3));

        transporterRequest = new TransporterToAssignRequest();
        transporterRequest.setTransporterId(transporterId);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShipment_Success() {
        setAuthentication(supplierUser, Role.SUPPLIER);

        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.of(item));
        when(shipmentRepo.save(any(Shipment.class))).thenReturn(shipment);

        ShipmentResponse result = shipmentService.createShipment(shipmentRequest);

        assertNotNull(result);
        assertEquals(shipmentId, result.getShipmentId());
        assertEquals(ShipmentStatus.CREATED, result.getCurrentStatus());

        verify(itemRepo).findByItemId(itemId);
        verify(shipmentRepo).save(any(Shipment.class));
    }

    @Test
    void createShipment_ItemNotFound_ThrowsException() {
        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.createShipment(shipmentRequest));

        verify(itemRepo).findByItemId(itemId);
        verify(shipmentRepo, never()).save(any());
    }

    @Test
    void createShipment_UserNotAuthorized_ThrowsException() {
        setAuthentication(adminUser, Role.ADMIN);

        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.of(item));

        assertThrows(UserNotMatch.class,
                () -> shipmentService.createShipment(shipmentRequest));

        verify(itemRepo).findByItemId(itemId);
        verify(shipmentRepo, never()).save(any());
    }

    @Test
    void assignTransporter_Success() {
        when(userRepo.findByUserId(transporterId)).thenReturn(Optional.of(transporterUser));
        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepo.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShipmentResponse result = shipmentService.assignTransporter(transporterRequest, shipmentId);

        assertNotNull(result);
        assertNotNull(result.getTransporterEmail());
        assertEquals(transporterUser.getEmail(), result.getTransporterEmail());

        verify(userRepo).findByUserId(transporterId);
        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(shipmentRepo).save(any(Shipment.class));
    }

    @Test
    void assignTransporter_TransporterNotFound_ThrowsException() {
        when(userRepo.findByUserId(transporterId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.assignTransporter(transporterRequest, shipmentId));

        verify(userRepo).findByUserId(transporterId);
        verify(shipmentRepo, never()).findByShipmentId(any());
        verify(shipmentRepo, never()).save(any());
    }

    @Test
    void assignTransporter_ShipmentNotFound_ThrowsException() {
        when(userRepo.findByUserId(transporterId)).thenReturn(Optional.of(transporterUser));
        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.assignTransporter(transporterRequest, shipmentId));

        verify(userRepo).findByUserId(transporterId);
        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(shipmentRepo, never()).save(any());
    }

    @Test
    void getAllShipments_Admin_Success() {
        setAuthentication(adminUser, Role.ADMIN);

        when(userRepo.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(adminUser));
        when(shipmentRepo.findAll()).thenReturn(List.of(shipment));

        List<ShipmentResponse> result = shipmentService.getAllShipments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(shipmentId, result.get(0).getShipmentId());

        verify(userRepo).findByEmail("admin@gmail.com");
        verify(shipmentRepo).findAll();
    }

    @Test
    void getAllShipments_Supplier_Success() {
        setAuthentication(supplierUser, Role.SUPPLIER);

        when(userRepo.findByEmail("supplier1@example.com"))
                .thenReturn(Optional.of(supplierUser));
        when(shipmentRepo.findByItem_Supplier_EmailIgnoreCase("supplier1@example.com"))
                .thenReturn(List.of(shipment));

        List<ShipmentResponse> result = shipmentService.getAllShipments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(shipmentId, result.get(0).getShipmentId());

        verify(userRepo).findByEmail("supplier1@example.com");
        verify(shipmentRepo).findByItem_Supplier_EmailIgnoreCase("supplier1@example.com");
    }

    @Test
    void getAllShipments_Transporter_Success() {
        setAuthentication(transporterUser, Role.TRANSPORTER);

        when(userRepo.findByEmail("transporter1@example.com"))
                .thenReturn(Optional.of(transporterUser));
        when(shipmentRepo.findByAssignedTransporter_EmailIgnoreCase("transporter1@example.com"))
                .thenReturn(List.of(shipment));

        List<ShipmentResponse> result = shipmentService.getAllShipments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(shipmentId, result.get(0).getShipmentId());

        verify(userRepo).findByEmail("transporter1@example.com");
        verify(shipmentRepo).findByAssignedTransporter_EmailIgnoreCase("transporter1@example.com");
    }

    @Test
    void changeShipmentStatus_Admin_Success() {
        setAuthentication(adminUser, Role.ADMIN);

        when(userRepo.findByEmail("admin@gmail.com")).thenReturn(Optional.of(adminUser));
        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepo.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShipmentStatusChangeResponse result = shipmentService.changeShipmentStatus(shipmentId, ShipmentStatus.IN_TRANSIT);

        assertNotNull(result);
        assertEquals(shipmentId, result.getShipmentId());
        assertEquals(ShipmentStatus.IN_TRANSIT, result.getCurrentStatus());

        verify(userRepo).findByEmail("admin@gmail.com");
        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(shipmentRepo).save(any(Shipment.class));
    }

    @Test
    void changeShipmentStatus_Transporter_Success() {
        setAuthentication(transporterUser, Role.TRANSPORTER);

        when(userRepo.findByEmail("transporter1@example.com")).thenReturn(Optional.of(transporterUser));
        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepo.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShipmentStatusChangeResponse result = shipmentService.changeShipmentStatus(shipmentId, ShipmentStatus.IN_TRANSIT);

        assertNotNull(result);
        assertEquals(shipmentId, result.getShipmentId());
        assertEquals(ShipmentStatus.IN_TRANSIT, result.getCurrentStatus());

        verify(userRepo).findByEmail("transporter1@example.com");
        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(shipmentRepo).save(any(Shipment.class));
    }

    @Test
    void changeShipmentStatus_ShipmentNotFound_ThrowsException() {
        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.changeShipmentStatus(shipmentId, ShipmentStatus.IN_TRANSIT));

        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(shipmentRepo, never()).save(any());
    }

    private void setAuthentication(UserEntity user, Role role) {
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}

