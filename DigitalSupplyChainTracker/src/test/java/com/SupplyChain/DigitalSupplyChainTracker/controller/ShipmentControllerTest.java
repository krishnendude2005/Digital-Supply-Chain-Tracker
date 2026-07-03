package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentStatusChangeRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.TransporterToAssignRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import com.SupplyChain.DigitalSupplyChainTracker.service.ShipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShipmentControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @Mock
    private ShipmentService shipmentService;

    @InjectMocks
    private ShipmentController shipmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(shipmentController).build();
    }

    @Test
    void createShipment_Success() throws Exception {
        ShipmentRequest shipmentRequest = new ShipmentRequest();
        shipmentRequest.setItemId(UUID.randomUUID());
        shipmentRequest.setFromLocation("Warehouse A");
        shipmentRequest.setToLocation("Warehouse B");
        shipmentRequest.setEndDate(LocalDateTime.now().plusDays(3));

        Shipment shipment = Shipment.builder()
                .id(1L)
                .shipmentId(UUID.randomUUID())
                .item(null)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .shipmentStartDate(null)
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(ShipmentStatus.CREATED)
                .assignedTransporter(null)
                .build();

        when(shipmentService.createShipment(any(ShipmentRequest.class))).thenReturn(shipment);

        mockMvc.perform(post("/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shipmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fromLocation").value("Warehouse A"))
                .andExpect(jsonPath("$.toLocation").value("Warehouse B"))
                .andExpect(jsonPath("$.currentStatus").value("CREATED"));
    }

    @Test
    void assignTransporter_Success() throws Exception {
        UUID shipmentId = UUID.randomUUID();
        UUID transporterId = UUID.randomUUID();

        TransporterToAssignRequest request = new TransporterToAssignRequest();
        request.setTransporterId(transporterId);

        UserEntity transporter = buildUser("transporter1@example.com", Role.TRANSPORTER, transporterId);

        Shipment shipment = Shipment.builder()
                .id(1L)
                .shipmentId(shipmentId)
                .item(null)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .shipmentStartDate(LocalDateTime.now())
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .assignedTransporter(transporter)
                .build();

        when(shipmentService.assignTransporter(any(TransporterToAssignRequest.class), eq(shipmentId)))
                .thenReturn(shipment);

        mockMvc.perform(post("/shipments/{shipmentId}/assign", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId").value(shipmentId.toString()))
                .andExpect(jsonPath("$.assignedTransporter.email").value("transporter1@example.com"));
    }

    @Test
    void getAllShipments_Admin_Success() throws Exception {
        Shipment shipment1 = buildShipment("Warehouse A", "Warehouse B", ShipmentStatus.CREATED, null);
        Shipment shipment2 = buildShipment("Warehouse C", "Warehouse D", ShipmentStatus.IN_TRANSIT, null);

        when(shipmentService.getAllShipments()).thenReturn(List.of(shipment1, shipment2));

        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllShipments_Supplier_Success() throws Exception {
        Shipment shipment1 = buildShipment("Warehouse A", "Warehouse B", ShipmentStatus.CREATED, null);
        Shipment shipment2 = buildShipment("Warehouse C", "Warehouse D", ShipmentStatus.IN_TRANSIT, null);

        when(shipmentService.getAllShipments()).thenReturn(List.of(shipment1, shipment2));

        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllShipments_Transporter_Success() throws Exception {
        UserEntity transporter = buildUser("transporter1@example.com", Role.TRANSPORTER, UUID.randomUUID());

        Shipment shipment1 = buildShipment("Warehouse A", "Warehouse B", ShipmentStatus.CREATED, transporter);
        Shipment shipment2 = buildShipment("Warehouse C", "Warehouse D", ShipmentStatus.IN_TRANSIT, transporter);

        when(shipmentService.getAllShipments()).thenReturn(List.of(shipment1, shipment2));

        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void changeShipmentStatus_Admin_Success() throws Exception {
        UUID shipmentId = UUID.randomUUID();

        ShipmentStatusChangeRequest request = new ShipmentStatusChangeRequest();
        request.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentService.changeShipmentStatus(eq(shipmentId), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(true);

        mockMvc.perform(put("/shipments/{shipmentId}/status", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Shipment status changed successfully")))
                .andExpect(content().string(containsString(shipmentId.toString())))
                .andExpect(content().string(containsString("IN_TRANSIT")));
    }

    @Test
    void changeShipmentStatus_Transporter_Success() throws Exception {
        UUID shipmentId = UUID.randomUUID();

        ShipmentStatusChangeRequest request = new ShipmentStatusChangeRequest();
        request.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentService.changeShipmentStatus(eq(shipmentId), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(true);

        mockMvc.perform(put("/shipments/{shipmentId}/status", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Shipment status changed successfully")))
                .andExpect(content().string(containsString(shipmentId.toString())))
                .andExpect(content().string(containsString("IN_TRANSIT")));
    }

    private UserEntity buildUser(String email, Role role, UUID userId) {
        return UserEntity.builder()
                .id(1L)
                .userId(userId)
                .name(role.name() + " User")
                .email(email)
                .password("encodedPassword")
                .role(role)
                .build();
    }

    private Shipment buildShipment(String from, String to, ShipmentStatus status, UserEntity transporter) {
        return Shipment.builder()
                .id(1L)
                .shipmentId(UUID.randomUUID())
                .item(null)
                .fromLocation(from)
                .toLocation(to)
                .shipmentStartDate(LocalDateTime.now())
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(status)
                .assignedTransporter(transporter)
                .build();
    }
}

