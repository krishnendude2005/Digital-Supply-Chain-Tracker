package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.entity.Alert;

import java.util.List;
import java.util.UUID;

public interface AlertService {
    List<Alert> findAllAlerts();
    List<Alert> findAllByShipmentId(UUID shipmentId);
}
