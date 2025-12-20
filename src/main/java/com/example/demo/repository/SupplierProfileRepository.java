package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.SupplierProfile;

public interface SupplierProfileRepository
        extends JpaRepository<SupplierProfile, Long> {
}