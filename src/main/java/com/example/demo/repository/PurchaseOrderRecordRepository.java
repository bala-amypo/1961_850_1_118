package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.PurchaseOrderRecord;

public interface PurchaseOrderRecordRepository
        extends JpaRepository<PurchaseOrderRecord, Long> {
}