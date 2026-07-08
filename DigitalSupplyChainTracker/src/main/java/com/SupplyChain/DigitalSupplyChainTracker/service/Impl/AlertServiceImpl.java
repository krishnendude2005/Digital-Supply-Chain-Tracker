package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;
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
    public List<Alert> findAllAlerts() {
        return alertRepo.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Alert> findAllByShipmentId(UUID shipmentId) {
        return alertRepo.findAllByCheckpointLog_Shipment_ShipmentId(shipmentId);
    }


}
