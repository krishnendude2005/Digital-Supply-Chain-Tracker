package com.SupplyChain.DigitalSupplyChainTracker.controller;


import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.TransporterToAssignRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<?> createShipment(@RequestBody ShipmentRequest shipmentRequest) {

       Shipment newlyCreatedShipment = shipmentService.createShipment(shipmentRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newlyCreatedShipment);

    }

    @PostMapping("/{shipmentId}/assign")
    public ResponseEntity<?> assignShipment(@RequestBody TransporterToAssignRequest transporterToAssign, @PathVariable UUID shipmentId) {

       Shipment assignedShipment =  shipmentService.assignTransporter(transporterToAssign, shipmentId);

       return ResponseEntity.status(HttpStatus.OK).body(assignedShipment);
    }

    @GetMapping
    public ResponseEntity<?> getAllShipments() {
        return ResponseEntity.status(HttpStatus.OK).body(shipmentService.getAllShipments());
    }




}
