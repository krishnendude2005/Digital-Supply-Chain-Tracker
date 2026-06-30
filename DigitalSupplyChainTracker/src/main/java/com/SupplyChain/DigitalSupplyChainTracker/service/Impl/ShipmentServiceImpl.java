package com.SupplyChain.DigitalSupplyChainTracker.service.Impl;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ShipmentRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.TransporterToAssignRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Shipment;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.ShipmentStatus;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ResourceNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.exception.UserNotMatch;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ItemRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ItemRepo itemRepo;
    private final ShipmentRepo shipmentRepo;
    private final UserRepo userRepo;

    @PreAuthorize("hasRole('SUPPLIER')")
    @Override
    public Shipment createShipment(ShipmentRequest shipmentRequest) {


        //Fetch the Item from DB
        Item item = itemRepo.findByItemId(shipmentRequest.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + shipmentRequest.getItemId()));


        //Need to check if the current item supplier is the same as the supplier trying to create the shipment
        String currentSupplierEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!item.getSupplier().getEmail().equals(currentSupplierEmail)) {
            throw new UserNotMatch(
                    String.format(
                            "User '%s' is not authorized to create a shipment for item '%s' (ID: %s). The item belongs to supplier '%s'.",
                            currentSupplierEmail,
                            item.getName(),
                            item.getItemId(),
                            item.getSupplier().getEmail()
                    )
            );
        }


        //Create the Initial Shipment (Made by SUPPLIER)
        Shipment newShipment = Shipment.builder()
                .shipmentId(UUID.randomUUID())
                .item(item)
                .fromLocation(shipmentRequest.getFromLocation())
                .toLocation(shipmentRequest.getToLocation())
                .currentStatus(ShipmentStatus.CREATED)
                .build();

        //Save the shipment in DB
        return shipmentRepo.save(newShipment);


    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Shipment assignTransporter(TransporterToAssignRequest transporter, UUID shipmentId) {

        UUID transporterId = transporter.getTransporterId();
        UserEntity transporterToAssign = userRepo.findByUserId(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found with id: " + transporter.getTransporterId()));

        Shipment existingShipmentToAssignTransporter = shipmentRepo.findByShipmentId(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + shipmentId));

        //Assign transporter
        existingShipmentToAssignTransporter.setAssignedTransporter(transporterToAssign);

        return shipmentRepo.save(existingShipmentToAssignTransporter);
    }

    //todo: need role based filtering
    @Override
    public List<Shipment> getAllShipments() {
        return shipmentRepo.findAll();
    }


}
