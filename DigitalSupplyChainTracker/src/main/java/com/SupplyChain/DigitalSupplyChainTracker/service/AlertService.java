package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AlertResponse;

import java.util.List;
import java.util.UUID;

public interface AlertService {
    List<AlertResponse> findAllAlerts();
    List<AlertResponse> findAllByShipmentId(UUID shipmentId);
}
