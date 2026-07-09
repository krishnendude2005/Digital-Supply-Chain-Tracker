package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.response.AlertResponse;
import com.SupplyChain.DigitalSupplyChainTracker.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping()
    public ResponseEntity<?> getAllAlerts() {
        List<AlertResponse> alerts = alertService.findAllAlerts();
        return ResponseEntity.status(HttpStatus.OK).body(alerts);
    }

    @GetMapping("/{shipmentId}")
    public ResponseEntity<?> getAllForShipment(@PathVariable UUID shipmentId) {
        List<AlertResponse> alerts = alertService.findAllByShipmentId(shipmentId);
        return ResponseEntity.status(HttpStatus.OK).body(alerts);
    }
}
