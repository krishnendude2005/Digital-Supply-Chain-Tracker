package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ItemStatus;
import com.SupplyChain.DigitalSupplyChainTracker.service.CheckPointLogService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CheckPointLogControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CheckPointLogService checkPointLogService;

    @InjectMocks
    private CheckPointLogController checkPointLogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(checkPointLogController).build();
    }

    @Test
    void createCheckpoint_Success() throws Exception {
        UUID shipmentId = UUID.randomUUID();

        CreateCheckpointLogRequest request = new CreateCheckpointLogRequest();
        request.setShipmentId(shipmentId);
        request.setLocation("Kolkata Hub");
        request.setItemStatus(ItemStatus.IN_TRANSIT);

        CreateCheckPointLogResponse response = CreateCheckPointLogResponse.builder()
                .message("Checkpoint created successfully")
                .shipmentId(shipmentId)
                .location("Kolkata Hub")
                .itemStatus(ItemStatus.IN_TRANSIT)
                .date(LocalDateTime.now())
                .build();

        when(checkPointLogService.createCheckPointLog(any(CreateCheckpointLogRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/checkpoints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Checkpoint created successfully"))
                .andExpect(jsonPath("$.shipmentId").value(shipmentId.toString()))
                .andExpect(jsonPath("$.location").value("Kolkata Hub"))
                .andExpect(jsonPath("$.itemStatus").value("IN_TRANSIT"));
    }
    @Test
    void getShipmentLog_shouldReturn200AndCheckpointLogs() throws Exception {
        UUID shipmentId = UUID.randomUUID();

        CheckpointLog log1 = CheckpointLog.builder()
                .id(1L)
                .checkpointId(UUID.randomUUID())
                .location("Kolkata Hub")
                .itemStatus(ItemStatus.IN_TRANSIT)
                .timestamp(LocalDateTime.now())
                .build();

        CheckpointLog log2 = CheckpointLog.builder()
                .id(2L)
                .checkpointId(UUID.randomUUID())
                .location("Delhi Hub")
                .itemStatus(ItemStatus.DELIVERED)
                .timestamp(LocalDateTime.now())
                .build();

        when(checkPointLogService.getShipmentLog(shipmentId))
                .thenReturn(List.of(log1, log2));

        mockMvc.perform(get("/checkpoints/shipment/{shipmentId}", shipmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].location").value("Kolkata Hub"))
                .andExpect(jsonPath("$[0].itemStatus").value("IN_TRANSIT"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].location").value("Delhi Hub"))
                .andExpect(jsonPath("$[1].itemStatus").value("DELIVERED"));
    }
}

