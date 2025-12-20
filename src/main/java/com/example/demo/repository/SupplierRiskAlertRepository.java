package com.example.demo.repository;

import com.example.demo.model.SupplierRiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRiskAlertRepository extends JpaRepository<SupplierRiskAlert, Long> {
    List<SupplierRiskAlert> findBySupplierId(Long supplierId);
    List<SupplierRiskAlert> findByResolvedFalse();
}