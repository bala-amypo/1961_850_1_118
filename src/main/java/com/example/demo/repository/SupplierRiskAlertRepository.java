package com.example.demo.repository;

import com.example.demo.model.SupplierRiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupplierRiskAlertRepository extends JpaRepository<SupplierRiskAlert, Long> {
    List<SupplierRiskAlert> findBySupplierId(Long supplierId);
}