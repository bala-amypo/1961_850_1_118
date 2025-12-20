package com.example.demo.controller;

import com.example.demo.model.DeliveryRecord;
import com.example.demo.service.DeliveryRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Delivery Records", description = "Operations for managing delivery records")
public class DeliveryRecordController {

    private final DeliveryRecordService deliveryRecordService;

    public DeliveryRecordController(DeliveryRecordService deliveryRecordService) {
        this.deliveryRecordService = deliveryRecordService;
    }

    @PostMapping
    @Operation(summary = "Record delivery", description = "Records a new delivery against a purchase order")
    public ResponseEntity<DeliveryRecord> recordDelivery(@Valid @RequestBody DeliveryRecord delivery) {
        DeliveryRecord created = deliveryRecordService.recordDelivery(delivery);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/po/{poId}")
    @Operation(summary = "Get deliveries for PO", description = "Retrieves all delivery records for a specific purchase order")
    public ResponseEntity<List<DeliveryRecord>> getDeliveriesByPO(@Parameter(description = "Purchase Order ID") @PathVariable Long poId) {
        List<DeliveryRecord> deliveries = deliveryRecordService.getDeliveriesByPO(poId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping
    @Operation(summary = "Get all deliveries", description = "Retrieves all delivery records in the system")
    public ResponseEntity<List<DeliveryRecord>> getAllDeliveries() {
        List<DeliveryRecord> deliveries = deliveryRecordService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }
}