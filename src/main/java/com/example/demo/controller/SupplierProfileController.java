package com.example.demo.controller;

import com.example.demo.model.SupplierProfile;
import com.example.demo.service.SupplierProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Management", description = "Operations for managing supplier profiles")
public class SupplierProfileController {

    private final SupplierProfileService supplierProfileService;

    public SupplierProfileController(SupplierProfileService supplierProfileService) {
        this.supplierProfileService = supplierProfileService;
    }

    @PostMapping
    @Operation(summary = "Create new supplier", description = "Creates a new supplier profile with unique supplier code")
    public ResponseEntity<SupplierProfile> createSupplier(@Valid @RequestBody SupplierProfile supplier) {
        SupplierProfile created = supplierProfileService.createSupplier(supplier);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Retrieves a supplier profile by its unique ID")
    public ResponseEntity<SupplierProfile> getSupplierById(@Parameter(description = "Supplier ID") @PathVariable Long id) {
        SupplierProfile supplier = supplierProfileService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Retrieves all supplier profiles in the system")
    public ResponseEntity<List<SupplierProfile>> getAllSuppliers() {
        List<SupplierProfile> suppliers = supplierProfileService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/lookup/{supplierCode}")
    @Operation(summary = "Lookup supplier by code", description = "Finds a supplier by their unique supplier code")
    public ResponseEntity<SupplierProfile> getSupplierByCode(@Parameter(description = "Supplier Code") @PathVariable String supplierCode) {
        return supplierProfileService.getBySupplierCode(supplierCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update supplier status", description = "Updates the active/inactive status of a supplier")
    public ResponseEntity<SupplierProfile> updateSupplierStatus(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            @Parameter(description = "Active status") @RequestParam boolean active) {
        SupplierProfile updated = supplierProfileService.updateSupplierStatus(id, active);
        return ResponseEntity.ok(updated);
    }
}