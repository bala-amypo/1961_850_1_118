package com.example.demo.controller;

import com.example.demo.model.PurchaseOrderRecord;
import com.example.demo.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@Tag(name = "Purchase Orders", description = "Operations for managing purchase orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    @Operation(summary = "Create purchase order", description = "Creates a new purchase order for an active supplier")
    public ResponseEntity<PurchaseOrderRecord> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRecord po) {
        PurchaseOrderRecord created = purchaseOrderService.createPurchaseOrder(po);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID", description = "Retrieves a purchase order by its unique ID")
    public ResponseEntity<PurchaseOrderRecord> getPOById(@Parameter(description = "Purchase Order ID") @PathVariable Long id) {
        return purchaseOrderService.getPOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get POs for supplier", description = "Retrieves all purchase orders for a specific supplier")
    public ResponseEntity<List<PurchaseOrderRecord>> getPOsBySupplier(@Parameter(description = "Supplier ID") @PathVariable Long supplierId) {
        List<PurchaseOrderRecord> pos = purchaseOrderService.getPOsBySupplier(supplierId);
        return ResponseEntity.ok(pos);
    }

    @GetMapping
    @Operation(summary = "Get all purchase orders", description = "Retrieves all purchase orders in the system")
    public ResponseEntity<List<PurchaseOrderRecord>> getAllPurchaseOrders() {
        List<PurchaseOrderRecord> pos = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(pos);
    }
}