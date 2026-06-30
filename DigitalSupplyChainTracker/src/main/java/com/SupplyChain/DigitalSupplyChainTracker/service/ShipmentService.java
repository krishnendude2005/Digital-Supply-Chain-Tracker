package com.SupplyChain.DigitalSupplyChainTracker.service;


import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.TransporterToAssignRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
    Shipment createShipment(ShipmentRequest shipmentRequest);
    Shipment assignTransporter(TransporterToAssignRequest transporter, UUID shipmentId);
    List<Shipment> getAllShipments();
}
