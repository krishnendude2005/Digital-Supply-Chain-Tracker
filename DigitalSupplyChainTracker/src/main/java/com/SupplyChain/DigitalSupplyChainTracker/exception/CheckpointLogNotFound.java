package com.SupplyChain.DigitalSupplyChainTracker.exception;

public class CheckpointLogNotFound extends RuntimeException {
    public CheckpointLogNotFound(String message) {
        super(message);
    }
}
