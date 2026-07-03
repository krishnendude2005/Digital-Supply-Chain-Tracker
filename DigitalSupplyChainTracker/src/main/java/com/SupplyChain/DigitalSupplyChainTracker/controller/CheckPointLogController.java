package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.CreateCheckpointLogRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.CreateCheckPointLogResponse;
import com.SupplyChain.DigitalSupplyChainTracker.service.CheckPointLogService;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.CheckpointLogServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkpoints")
@RequiredArgsConstructor
public class CheckPointLogController {

    private final CheckPointLogService checkPointLogService;

    @PostMapping()
    public ResponseEntity<?> createCheckpoint(@RequestBody CreateCheckpointLogRequest checkpointLogRequest) {

        CreateCheckPointLogResponse response = checkPointLogService.createCheckPointLog(checkpointLogRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
