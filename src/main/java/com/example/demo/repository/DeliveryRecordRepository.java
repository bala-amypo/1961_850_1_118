package com.example.demo.repository;

import com.example.demo.model.DeliveryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryRecordRepository extends JpaRepository<DeliveryRecord, Long> {
    List<DeliveryRecord> findByPoId(Long poId);
}