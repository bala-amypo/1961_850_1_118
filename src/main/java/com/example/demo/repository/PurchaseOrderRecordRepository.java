package com.example.demo.repository;

import com.example.demo.model.PurchaseOrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRecordRepository extends JpaRepository<PurchaseOrderRecord, Long> {
    List<PurchaseOrderRecord> findBySupplierId(Long supplierId);
}