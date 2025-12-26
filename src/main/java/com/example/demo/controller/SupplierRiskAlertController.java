package com.example.demo.controller;

import com.example.demo.model.SupplierRiskAlert;
import com.example.demo.service.SupplierRiskAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-alerts")
@Tag(name = "Risk Alerts", description = "Operations for managing supplier risk alerts")
public class SupplierRiskAlertController {

    private final SupplierRiskAlertService riskAlertService;

    public SupplierRiskAlertController(SupplierRiskAlertService riskAlertService) {
        this.riskAlertService = riskAlertService;
    }

    @PostMapping
    @Operation(summary = "Create risk alert", description = "Creates a new risk alert for a supplier")
    public ResponseEntity<SupplierRiskAlert> createAlert(@Valid @RequestBody SupplierRiskAlert alert) {
        SupplierRiskAlert created = riskAlertService.createAlert(alert);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get alerts for supplier", description = "Retrieves all risk alerts for a specific supplier")
    public ResponseEntity<List<SupplierRiskAlert>> getAlertsBySupplier(@Parameter(description = "Supplier ID") @PathVariable Long supplierId) {
        List<SupplierRiskAlert> alerts = riskAlertService.getAlertsBySupplier(supplierId);
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "Resolve alert", description = "Marks a risk alert as resolved")
    public ResponseEntity<SupplierRiskAlert> resolveAlert(@Parameter(description = "Alert ID") @PathVariable Long id) {
        SupplierRiskAlert resolved = riskAlertService.resolveAlert(id);
        return ResponseEntity.ok(resolved);
    }

    @GetMapping
    @Operation(summary = "Get all risk alerts", description = "Retrieves all risk alerts in the system")
    public ResponseEntity<List<SupplierRiskAlert>> getAllAlerts() {
        List<SupplierRiskAlert> alerts = riskAlertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }
}1