package com.example.demo.repository;

import com.example.demo.model.SupplierProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierProfileRepository extends JpaRepository<SupplierProfile, Long> {
    Optional<SupplierProfile> findBySupplierCode(String supplierCode);
    List<SupplierProfile> findByActiveTrue();
}