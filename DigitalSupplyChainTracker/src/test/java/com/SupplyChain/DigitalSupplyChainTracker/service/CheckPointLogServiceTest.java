package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CheckpointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ResourceNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.CheckpointLogRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.CheckpointLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckPointLogServiceTest {

    @Mock
    private CheckpointLogRepo checkpointLogRepo;

    @Mock
    private ShipmentRepo shipmentRepo;

    @InjectMocks
    private CheckpointLogServiceImpl checkpointLogService;

    private CreateCheckpointLogRequest createCheckpointLogRequest;
    private CheckpointLog checkpointLog;
    private UUID shipmentId;

    @BeforeEach
    void setUp() {
        shipmentId = UUID.randomUUID();

        createCheckpointLogRequest = new CreateCheckpointLogRequest();
        createCheckpointLogRequest.setShipmentId(shipmentId);
        createCheckpointLogRequest.setLocation("Warehouse A");
        createCheckpointLogRequest.setItemStatus(ItemStatus.IN_TRANSIT);

        checkpointLog = CheckpointLog.builder()
                .checkpointId(UUID.randomUUID())
                .location("Warehouse A")
                .itemStatus(ItemStatus.IN_TRANSIT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void createCheckPointLog_Success() {
        CheckpointLog checkpointLogWithShipment = CheckpointLog.builder()
                .checkpointId(UUID.randomUUID())
                .location("Warehouse A")
                .itemStatus(ItemStatus.IN_TRANSIT)
                .timestamp(LocalDateTime.now())
                .build();

        Shipment shipment = Shipment.builder()
                .shipmentId(shipmentId)
                .build();

        checkpointLogWithShipment.setShipment(shipment);

        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.of(shipment));
        when(checkpointLogRepo.save(any(CheckpointLog.class))).thenReturn(checkpointLogWithShipment);

        CreateCheckPointLogResponse result = checkpointLogService.createCheckPointLog(createCheckpointLogRequest);

        assertNotNull(result);
        assertEquals(ItemStatus.IN_TRANSIT, result.getItemStatus());
        assertEquals("Warehouse A", result.getLocation());
        assertEquals(shipmentId, result.getShipmentId());
        assertEquals("Checkpoint Log Created Successfully", result.getMessage());

        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(checkpointLogRepo).save(any(CheckpointLog.class));
    }

    @Test
    void createCheckPointLog_ShipmentNotFound_ThrowsException() {
        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> checkpointLogService.createCheckPointLog(createCheckpointLogRequest));

        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(checkpointLogRepo, never()).save(any());
    }

    @Test
    void createCheckPointLog_ValidRequest_WithShipment() {
        Shipment shipment = Shipment.builder()
                .shipmentId(shipmentId)
                .build();

        checkpointLog.setShipment(shipment);

        when(shipmentRepo.findByShipmentId(shipmentId)).thenReturn(Optional.of(shipment));
        when(checkpointLogRepo.save(any(CheckpointLog.class))).thenReturn(checkpointLog);

        CreateCheckPointLogResponse result = checkpointLogService.createCheckPointLog(createCheckpointLogRequest);

        assertNotNull(result);
        assertEquals(ItemStatus.IN_TRANSIT, result.getItemStatus());
        assertEquals("Warehouse A", result.getLocation());
        assertEquals(shipmentId, result.getShipmentId());

        verify(shipmentRepo).findByShipmentId(shipmentId);
        verify(checkpointLogRepo).save(any(CheckpointLog.class));
    }

    @Test
    void getShipmentLog_shouldReturnCheckpointLogs() {
        UUID shipmentId = UUID.randomUUID();

        Shipment shipment = Shipment.builder()
                .id(1L)
                .shipmentId(shipmentId)
                .fromLocation("Kolkata")
                .toLocation("Delhi")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CheckpointLog log1 = CheckpointLog.builder()
                .id(1L)
                .checkpointId(UUID.randomUUID())
                .location("Kolkata Hub")
                .itemStatus(ItemStatus.IN_TRANSIT)
                .shipment(shipment)
                .timestamp(LocalDateTime.now())
                .build();

        CheckpointLog log2 = CheckpointLog.builder()
                .id(2L)
                .checkpointId(UUID.randomUUID())
                .location("Delhi Hub")
                .itemStatus(ItemStatus.DELIVERED)
                .shipment(shipment)
                .timestamp(LocalDateTime.now())
                .build();

        List<CheckpointLog> expectedLogs = List.of(log1, log2);

        when(checkpointLogRepo.findAllByShipment_ShipmentId(shipmentId)).thenReturn(expectedLogs);

        List<CheckpointLogResponse> result = checkpointLogService.getShipmentLog(shipmentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Kolkata Hub", result.get(0).getLocation());
        assertEquals(ItemStatus.IN_TRANSIT, result.get(0).getItemStatus());
        assertEquals("Delhi Hub", result.get(1).getLocation());
        assertEquals(ItemStatus.DELIVERED, result.get(1).getItemStatus());

        verify(checkpointLogRepo, times(1)).findAllByShipment_ShipmentId(shipmentId);
    }
}
