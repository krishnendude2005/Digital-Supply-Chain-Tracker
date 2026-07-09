package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AlertResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.AlertType;
import com.SupplyChain.DigitalSupplyChainTracker.repository.AlertRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepo alertRepo;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<AlertResponse> findAllAlerts() {
        return alertRepo.findAll().stream()
                .map(this::convertToAlertResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<AlertResponse> findAllByShipmentId(UUID shipmentId) {
        return alertRepo.findAllByCheckpointLog_Shipment_ShipmentId(shipmentId).stream()
                .map(this::convertToAlertResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<AlertResponse> findAllByAlertType(AlertType alertType) {
        return
                alertRepo.findAllByType(alertType)
                        .stream()
                        .map(this::convertToAlertResponse)
                        .toList();
    }

    private AlertResponse convertToAlertResponse(Alert alert) {
        return AlertResponse.builder()
                .alertId(alert.getAlertId())
                .type(alert.getType())
                .message(alert.getMessage())
                .resolved(alert.getResolved())
                .checkpointId(alert.getCheckpointLog() != null ? alert.getCheckpointLog().getCheckpointId() : null)
                .shipmentId(alert.getCheckpointLog() != null && alert.getCheckpointLog().getShipment() != null
                        ? alert.getCheckpointLog().getShipment().getShipmentId() : null)
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
