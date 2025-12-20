package com.example.demo.controller;

import com.example.demo.model.DelayScoreRecord;
import com.example.demo.service.DelayScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delay-scores")
@Tag(name = "Delay Scoring", description = "Operations for computing and managing delay scores")
public class DelayScoreController {

    private final DelayScoreService delayScoreService;

    public DelayScoreController(DelayScoreService delayScoreService) {
        this.delayScoreService = delayScoreService;
    }

    @PostMapping("/compute/{poId}")
    @Operation(summary = "Compute delay score for PO", description = "Computes delay score for a purchase order based on delivery records")
    public ResponseEntity<DelayScoreRecord> computeDelayScore(@Parameter(description = "Purchase Order ID") @PathVariable Long poId) {
        DelayScoreRecord score = delayScoreService.computeDelayScore(poId);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get scores for supplier", description = "Retrieves all delay scores for a specific supplier")
    public ResponseEntity<List<DelayScoreRecord>> getScoresBySupplier(@Parameter(description = "Supplier ID") @PathVariable Long supplierId) {
        List<DelayScoreRecord> scores = delayScoreService.getScoresBySupplier(supplierId);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get score by ID", description = "Retrieves a delay score by its unique ID")
    public ResponseEntity<DelayScoreRecord> getScoreById(@Parameter(description = "Delay Score ID") @PathVariable Long id) {
        return delayScoreService.getScoreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all delay scores", description = "Retrieves all delay scores in the system")
    public ResponseEntity<List<DelayScoreRecord>> getAllScores() {
        List<DelayScoreRecord> scores = delayScoreService.getAllScores();
        return ResponseEntity.ok(scores);
    }
}