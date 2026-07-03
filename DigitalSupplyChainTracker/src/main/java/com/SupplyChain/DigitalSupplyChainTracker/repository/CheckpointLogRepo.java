package com.SupplyChain.DigitalSupplyChainTracker.repository;

import com.SupplyChain.DigitalSupplyChainTracker.entity.CheckpointLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckpointLogRepo extends JpaRepository<CheckpointLog, Long> {
}
