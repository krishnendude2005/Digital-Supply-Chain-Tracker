package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
}

