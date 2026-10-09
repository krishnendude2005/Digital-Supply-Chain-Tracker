package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AlertResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import com.SupplyChain.DigitalSupplyChainTracker.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlertControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
    }

    @Test
    void getAllAlerts_Returns200WithAlerts() throws Exception {
        when(alertService.findAllAlerts())
                .thenReturn(List.of(buildResponse(AlertType.DELAYED), buildResponse(AlertType.DAMAGED)));

        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("DELAYED"))
                .andExpect(jsonPath("$[1].type").value("DAMAGED"));

        verify(alertService).findAllAlerts();
    }

    @Test
    void getAllAlerts_NoAlerts_Returns200WithEmptyList() throws Exception {
        when(alertService.findAllAlerts()).thenReturn(List.of());

        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllForShipment_Returns200WithAlerts() throws Exception {
        UUID shipmentId = UUID.randomUUID();
        AlertResponse response = buildResponse(AlertType.DELAYED);
        response.setShipmentId(shipmentId);

        when(alertService.findAllByShipmentId(shipmentId)).thenReturn(List.of(response));

        mockMvc.perform(get("/alerts/{shipmentId}", shipmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].shipmentId").value(shipmentId.toString()));

        verify(alertService).findAllByShipmentId(shipmentId);
    }

    @Test
    void getAlertsByAlertType_Returns200WithFilteredAlerts() throws Exception {
        when(alertService.findAllByAlertType(AlertType.DAMAGED))
                .thenReturn(List.of(buildResponse(AlertType.DAMAGED)));

        mockMvc.perform(get("/alerts").param("alertType", "DAMAGED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("DAMAGED"));

        verify(alertService).findAllByAlertType(AlertType.DAMAGED);
    }

    @Test
    void getAlertsByAlertType_NoMatch_Returns200WithEmptyList() throws Exception {
        when(alertService.findAllByAlertType(AlertType.DELAYED)).thenReturn(List.of());

        mockMvc.perform(get("/alerts").param("alertType", "DELAYED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAlertsByAlertType_InvalidType_Returns400() throws Exception {
        mockMvc.perform(get("/alerts").param("alertType", "NOT_A_TYPE"))
                .andExpect(status().isBadRequest());
    }

    private AlertResponse buildResponse(AlertType type) {
        return AlertResponse.builder()
                .alertId(UUID.randomUUID())
                .type(type)
                .message("Alert message")
                .resolved(false)
                .checkpointId(UUID.randomUUID())
                .shipmentId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
