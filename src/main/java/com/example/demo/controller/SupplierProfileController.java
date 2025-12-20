package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.demo.model.SupplierProfile;
import com.example.demo.service.SupplierProfileService;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierProfileController {

    private final SupplierProfileService service;

    public SupplierProfileController(SupplierProfileService service) {
        this.service = service;
    }

    @PostMapping
    public SupplierProfile create(@RequestBody SupplierProfile supplier) {
        return service.create(supplier);
    }

    @GetMapping
    public List<SupplierProfile> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SupplierProfile getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public SupplierProfile update(@PathVariable Long id,
                                  @RequestBody SupplierProfile supplier) {
        return service.update(id, supplier);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Supplier deleted successfully";
    }
}