package com.SupplyChain.DigitalSupplyChainTracker.service;


import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.TransporterToAssignRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ShipmentResponse;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ShipmentStatusChangeResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
    ShipmentResponse createShipment(ShipmentRequest shipmentRequest);
    ShipmentResponse assignTransporter(TransporterToAssignRequest transporter, UUID shipmentId);
    List<ShipmentResponse> getAllShipments();
    ShipmentStatusChangeResponse changeShipmentStatus(UUID shipmentId, ShipmentStatus status);
}
