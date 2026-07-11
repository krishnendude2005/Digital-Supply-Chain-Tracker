package com.SupplyChain.DigitalSupplyChainTracker.scheduler;

import com.SupplyChain.DigitalSupplyChainTracker.scheduler.service.ShipmentDelaySchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentDelayScheduler {
    private final ShipmentDelaySchedulerService shipmentDelaySchedulerService;


    @Scheduled(cron = "0 */30 * * * *" , zone = "Asia/Kolkata")
    public void checkDelayedShipments() {
        log.info("starting delayed shipment scheduler");
        shipmentDelaySchedulerService.scanAndCreateShipmentDelayAlerts();
        log.info("finished delayed shipment scheduler");
    }

}
