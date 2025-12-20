package com.example.demo.repository;

import com.example.demo.model.PurchaseOrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRecordRepository extends JpaRepository<PurchaseOrderRecord, Long> {
    List<PurchaseOrderRecord> findBySupplierId(Long supplierId);
    Optional<PurchaseOrderRecord> findByPoNumber(String poNumber);
}