package com.ussd_event_processor.repository;

import com.ussd_event_processor.entity.CdrLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CdrLogRepository extends JpaRepository<CdrLog, UUID> {
}