package com.SupplyChain.DigitalSupplyChainTracker.scheduler.service;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import com.SupplyChain.DigitalSupplyChainTracker.exception.CheckpointLogNotFound;
import com.SupplyChain.DigitalSupplyChainTracker.repository.AlertRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.CheckpointLogRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentDelaySchedulerServiceTest {

    @Mock
    private ShipmentRepo shipmentRepo;

    @Mock
    private AlertRepo alertRepo;

    @Mock
    private CheckpointLogRepo checkpointLogRepo;

    @InjectMocks
    private ShipmentDelaySchedulerService schedulerService;

    private UUID shipmentId;
    private Shipment delayedShipment;
    private CheckpointLog checkpointLog;

    @BeforeEach
    void setUp() {
        shipmentId = UUID.randomUUID();

        delayedShipment = Shipment.builder()
                .id(1L)
                .shipmentId(shipmentId)
                .fromLocation("Kolkata")
                .toLocation("Delhi")
                .shipmentExpectedDate(LocalDateTime.now().minusDays(1))
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .build();

        checkpointLog = CheckpointLog.builder()
                .id(1L)
                .checkpointId(UUID.randomUUID())
                .location("Kolkata Hub")
                .shipment(delayedShipment)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void scanAndCreateShipmentDelayAlerts_CreatesDelayedAlert_WhenNoUnresolvedAlertExists() {
        when(shipmentRepo.findByShipmentExpectedDateBeforeAndCurrentStatus(any(LocalDateTime.class), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(List.of(delayedShipment));
        when(alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(shipmentId, AlertType.DELAYED))
                .thenReturn(false);
        when(checkpointLogRepo.findTopByShipment_ShipmentIdOrderByTimestampDesc(shipmentId))
                .thenReturn(Optional.of(checkpointLog));

        schedulerService.scanAndCreateShipmentDelayAlerts();

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepo).save(alertCaptor.capture());

        Alert savedAlert = alertCaptor.getValue();
        assertNotNull(savedAlert.getAlertId());
        assertEquals(AlertType.DELAYED, savedAlert.getType());
        assertEquals("Shipment Delayed", savedAlert.getMessage());
        assertFalse(savedAlert.getResolved());
        assertEquals(checkpointLog, savedAlert.getCheckpointLog());

        verify(checkpointLogRepo).findTopByShipment_ShipmentIdOrderByTimestampDesc(shipmentId);
    }

    @Test
    void scanAndCreateShipmentDelayAlerts_DoesNotCreateDuplicate_WhenUnresolvedAlertExists() {
        when(shipmentRepo.findByShipmentExpectedDateBeforeAndCurrentStatus(any(LocalDateTime.class), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(List.of(delayedShipment));
        when(alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(shipmentId, AlertType.DELAYED))
                .thenReturn(true);

        schedulerService.scanAndCreateShipmentDelayAlerts();

        verify(alertRepo, never()).save(any(Alert.class));
        verify(checkpointLogRepo, never()).findTopByShipment_ShipmentIdOrderByTimestampDesc(any());
    }

    @Test
    void scanAndCreateShipmentDelayAlerts_DoesNothing_WhenNoDelayedShipments() {
        when(shipmentRepo.findByShipmentExpectedDateBeforeAndCurrentStatus(any(LocalDateTime.class), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(List.of());

        schedulerService.scanAndCreateShipmentDelayAlerts();

        verify(alertRepo, never()).save(any(Alert.class));
        verify(checkpointLogRepo, never()).findTopByShipment_ShipmentIdOrderByTimestampDesc(any());
        verify(alertRepo, never()).existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(any(), any());
    }

    @Test
    void scanAndCreateShipmentDelayAlerts_ThrowsCheckpointLogNotFound_WhenShipmentHasNoCheckpointLog() {
        when(shipmentRepo.findByShipmentExpectedDateBeforeAndCurrentStatus(any(LocalDateTime.class), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(List.of(delayedShipment));
        when(alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(shipmentId, AlertType.DELAYED))
                .thenReturn(false);
        when(checkpointLogRepo.findTopByShipment_ShipmentIdOrderByTimestampDesc(shipmentId))
                .thenReturn(Optional.empty());

        assertThrows(CheckpointLogNotFound.class,
                () -> schedulerService.scanAndCreateShipmentDelayAlerts());

        verify(alertRepo, never()).save(any(Alert.class));
    }

    @Test
    void scanAndCreateShipmentDelayAlerts_CreatesAlertForEachDelayedShipment() {
        UUID secondShipmentId = UUID.randomUUID();
        Shipment secondShipment = Shipment.builder()
                .id(2L)
                .shipmentId(secondShipmentId)
                .currentStatus(ShipmentStatus.IN_TRANSIT)
                .shipmentExpectedDate(LocalDateTime.now().minusDays(2))
                .build();
        CheckpointLog secondCheckpointLog = CheckpointLog.builder()
                .id(2L)
                .checkpointId(UUID.randomUUID())
                .shipment(secondShipment)
                .timestamp(LocalDateTime.now())
                .build();

        when(shipmentRepo.findByShipmentExpectedDateBeforeAndCurrentStatus(any(LocalDateTime.class), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(List.of(delayedShipment, secondShipment));
        when(alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(shipmentId, AlertType.DELAYED))
                .thenReturn(false);
        when(alertRepo.existsByCheckpointLog_Shipment_ShipmentIdAndTypeAndResolvedFalse(secondShipmentId, AlertType.DELAYED))
                .thenReturn(false);
        when(checkpointLogRepo.findTopByShipment_ShipmentIdOrderByTimestampDesc(shipmentId))
                .thenReturn(Optional.of(checkpointLog));
        when(checkpointLogRepo.findTopByShipment_ShipmentIdOrderByTimestampDesc(secondShipmentId))
                .thenReturn(Optional.of(secondCheckpointLog));

        schedulerService.scanAndCreateShipmentDelayAlerts();

        verify(alertRepo, times(2)).save(any(Alert.class));
    }
}
