package com.example.demo.service;

import com.example.demo.model.DelayScoreRecord;

import java.util.List;
import java.util.Optional;

public interface DelayScoreService {
    DelayScoreRecord computeDelayScore(Long poId);
    List<DelayScoreRecord> getScoresBySupplier(Long supplierId);
    Optional<DelayScoreRecord> getScoreById(Long id);
    List<DelayScoreRecord> getAllScores();
}