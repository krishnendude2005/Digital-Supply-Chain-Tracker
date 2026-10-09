package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AlertResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import com.SupplyChain.DigitalSupplyChainTracker.repository.AlertRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.AlertServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepo alertRepo;

    @InjectMocks
    private AlertServiceImpl alertService;

    private UUID shipmentId;
    private UUID checkpointId;
    private Alert alert;

    @BeforeEach
    void setUp() {
        shipmentId = UUID.randomUUID();
        checkpointId = UUID.randomUUID();

        Shipment shipment = Shipment.builder()
                .shipmentId(shipmentId)
                .build();

        CheckpointLog checkpointLog = CheckpointLog.builder()
                .checkpointId(checkpointId)
                .shipment(shipment)
                .build();

        alert = Alert.builder()
                .id(1L)
                .alertId(UUID.randomUUID())
                .type(AlertType.DELAYED)
                .message("Shipment Delayed")
                .resolved(false)
                .checkpointLog(checkpointLog)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAllAlerts_ReturnsMappedResponses() {
        when(alertRepo.findAll()).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.findAllAlerts();

        assertEquals(1, result.size());
        AlertResponse response = result.get(0);
        assertEquals(alert.getAlertId(), response.getAlertId());
        assertEquals(AlertType.DELAYED, response.getType());
        assertEquals("Shipment Delayed", response.getMessage());
        assertFalse(response.getResolved());
        assertEquals(checkpointId, response.getCheckpointId());
        assertEquals(shipmentId, response.getShipmentId());
        assertEquals(alert.getCreatedAt(), response.getCreatedAt());

        verify(alertRepo).findAll();
    }

    @Test
    void findAllAlerts_NoAlerts_ReturnsEmptyList() {
        when(alertRepo.findAll()).thenReturn(List.of());

        List<AlertResponse> result = alertService.findAllAlerts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(alertRepo).findAll();
    }

    @Test
    void findAllByShipmentId_ReturnsMappedResponses() {
        when(alertRepo.findAllByCheckpointLog_Shipment_ShipmentId(shipmentId))
                .thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.findAllByShipmentId(shipmentId);

        assertEquals(1, result.size());
        assertEquals(shipmentId, result.get(0).getShipmentId());
        assertEquals(checkpointId, result.get(0).getCheckpointId());

        verify(alertRepo).findAllByCheckpointLog_Shipment_ShipmentId(shipmentId);
    }

    @Test
    void findAllByAlertType_ReturnsMatchingAlerts() {
        Alert damagedAlert = Alert.builder()
                .alertId(UUID.randomUUID())
                .type(AlertType.DAMAGED)
                .message("Item Damaged")
                .resolved(false)
                .checkpointLog(alert.getCheckpointLog())
                .build();

        when(alertRepo.findAllByType(AlertType.DAMAGED)).thenReturn(List.of(damagedAlert));

        List<AlertResponse> result = alertService.findAllByAlertType(AlertType.DAMAGED);

        assertEquals(1, result.size());
        assertEquals(AlertType.DAMAGED, result.get(0).getType());
        assertEquals("Item Damaged", result.get(0).getMessage());

        verify(alertRepo).findAllByType(AlertType.DAMAGED);
    }

    @Test
    void findAllByAlertType_NoMatch_ReturnsEmptyList() {
        when(alertRepo.findAllByType(AlertType.DAMAGED)).thenReturn(List.of());

        List<AlertResponse> result = alertService.findAllByAlertType(AlertType.DAMAGED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(alertRepo).findAllByType(AlertType.DAMAGED);
    }

    @Test
    void findAllAlerts_AlertWithoutCheckpoint_MapsNullIdentifiers() {
        Alert orphanAlert = Alert.builder()
                .alertId(UUID.randomUUID())
                .type(AlertType.DAMAGED)
                .message("No checkpoint attached")
                .resolved(false)
                .build();

        when(alertRepo.findAll()).thenReturn(List.of(orphanAlert));

        AlertResponse response = alertService.findAllAlerts().get(0);

        assertNull(response.getCheckpointId());
        assertNull(response.getShipmentId());
        assertEquals(AlertType.DAMAGED, response.getType());
    }
}
