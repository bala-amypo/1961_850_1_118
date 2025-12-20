package com.example.demo.service.impl;

import org.springframework.stereotype.Service;
import java.util.List;

import com.example.demo.model.SupplierProfile;
import com.example.demo.repository.SupplierProfileRepository;
import com.example.demo.service.SupplierProfileService;
import com.example.demo.exception.ResourceNotFoundException;

@Service
public class SupplierProfileServiceImpl implements SupplierProfileService {

    private final SupplierProfileRepository repo;

    public SupplierProfileServiceImpl(SupplierProfileRepository repo) {
        this.repo = repo;
    }

    public SupplierProfile create(SupplierProfile supplier) {
        return repo.save(supplier);
    }

    public List<SupplierProfile> getAll() {
        return repo.findAll();
    }

    public SupplierProfile getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Supplier not found with id " + id));
    }

    public SupplierProfile update(Long id, SupplierProfile supplier) {
        SupplierProfile existing = getById(id);
        existing.supplierName = supplier.supplierName;
        existing.email = supplier.email;
        existing.phone = supplier.phone;
        return repo.save(existing);
    }

    public void delete(Long id) {
        SupplierProfile supplier = getById(id);
        repo.delete(supplier);
    }
}