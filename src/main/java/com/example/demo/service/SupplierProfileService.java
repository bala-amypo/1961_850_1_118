package com.example.demo.service;

import com.example.demo.model.SupplierProfile;
import java.util.List;

public interface SupplierProfileService {
    SupplierProfile create(SupplierProfile supplier);
    List<SupplierProfile> getAll();
    SupplierProfile getById(Long id);
    SupplierProfile update(Long id, SupplierProfile supplier);
    void delete(Long id);
}