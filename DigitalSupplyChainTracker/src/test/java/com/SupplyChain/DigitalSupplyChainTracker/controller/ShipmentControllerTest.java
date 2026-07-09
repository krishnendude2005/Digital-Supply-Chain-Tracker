package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentStatusChangeRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.TransporterToAssignRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ShipmentResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ShipmentStatusChangeResponse;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        ShipmentResponse shipmentResponse = ShipmentResponse.builder()
                .shipmentId(UUID.randomUUID())
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .currentStatus(ShipmentStatus.CREATED)
                .build();

        when(shipmentService.createShipment(any(ShipmentRequest.class))).thenReturn(shipmentResponse);

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

        ShipmentResponse shipmentResponse = ShipmentResponse.builder()
                .shipmentId(shipmentId)
                .fromLocation("Warehouse A")
                .toLocation("Warehouse B")
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .transporterEmail("transporter1@example.com")
                .build();

        when(shipmentService.assignTransporter(any(TransporterToAssignRequest.class), eq(shipmentId)))
                .thenReturn(shipmentResponse);

        mockMvc.perform(post("/shipments/{shipmentId}/assign", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId").value(shipmentId.toString()))
                .andExpect(jsonPath("$.transporterEmail").value("transporter1@example.com"));
    }

    @Test
    void getAllShipments_Admin_Success() throws Exception {
        ShipmentResponse shipment1 = buildShipmentResponse("Warehouse A", "Warehouse B", ShipmentStatus.CREATED);
        ShipmentResponse shipment2 = buildShipmentResponse("Warehouse C", "Warehouse D", ShipmentStatus.IN_TRANSIT);

        when(shipmentService.getAllShipments()).thenReturn(List.of(shipment1, shipment2));

        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllShipments_Supplier_Success() throws Exception {
        ShipmentResponse shipment1 = buildShipmentResponse("Warehouse A", "Warehouse B", ShipmentStatus.CREATED);
        ShipmentResponse shipment2 = buildShipmentResponse("Warehouse C", "Warehouse D", ShipmentStatus.IN_TRANSIT);

        when(shipmentService.getAllShipments()).thenReturn(List.of(shipment1, shipment2));

        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllShipments_Transporter_Success() throws Exception {
        ShipmentResponse shipment1 = buildShipmentResponse("Warehouse A", "Warehouse B", ShipmentStatus.CREATED);
        ShipmentResponse shipment2 = buildShipmentResponse("Warehouse C", "Warehouse D", ShipmentStatus.IN_TRANSIT);

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

        ShipmentStatusChangeResponse response = ShipmentStatusChangeResponse.builder()
                .message("Shipment status changed successfully")
                .shipmentId(shipmentId)
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .build();

        when(shipmentService.changeShipmentStatus(eq(shipmentId), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(response);

        mockMvc.perform(put("/shipments/{shipmentId}/status", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Shipment status changed successfully"))
                .andExpect(jsonPath("$.shipmentId").value(shipmentId.toString()))
                .andExpect(jsonPath("$.currentStatus").value("IN_TRANSIT"));
    }

    @Test
    void changeShipmentStatus_Transporter_Success() throws Exception {
        UUID shipmentId = UUID.randomUUID();

        ShipmentStatusChangeRequest request = new ShipmentStatusChangeRequest();
        request.setStatus(ShipmentStatus.IN_TRANSIT);

        ShipmentStatusChangeResponse response = ShipmentStatusChangeResponse.builder()
                .message("Shipment status changed successfully")
                .shipmentId(shipmentId)
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .build();

        when(shipmentService.changeShipmentStatus(eq(shipmentId), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(response);

        mockMvc.perform(put("/shipments/{shipmentId}/status", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Shipment status changed successfully"))
                .andExpect(jsonPath("$.shipmentId").value(shipmentId.toString()))
                .andExpect(jsonPath("$.currentStatus").value("IN_TRANSIT"));
    }

    private ShipmentResponse buildShipmentResponse(String from, String to, ShipmentStatus status) {
        return ShipmentResponse.builder()
                .shipmentId(UUID.randomUUID())
                .fromLocation(from)
                .toLocation(to)
                .shipmentStartDate(LocalDateTime.now())
                .shipmentExpectedDate(LocalDateTime.now().plusDays(3))
                .currentStatus(status)
                .build();
    }
}
