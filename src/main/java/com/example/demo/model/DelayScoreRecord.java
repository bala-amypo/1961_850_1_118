package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delay_score_records")
public class DelayScoreRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long supplierId;

    @Column(nullable = false, unique = true)
    private Long poId;

    @Column(nullable = false)
    private Integer delayDays;

    @Column(nullable = false, length = 20)
    private String delaySeverity;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private LocalDateTime computedAt;

    public DelayScoreRecord() {}

    public DelayScoreRecord(Long supplierId, Long poId, Integer delayDays, String delaySeverity, Double score) {
        this.supplierId = supplierId;
        this.poId = poId;
        this.delayDays = delayDays;
        this.delaySeverity = delaySeverity;
        this.score = score;
    }

    @PrePersist
    protected void onCreate() {
        computedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Long getPoId() { return poId; }
    public void setPoId(Long poId) { this.poId = poId; }

    public Integer getDelayDays() { return delayDays; }
    public void setDelayDays(Integer delayDays) { this.delayDays = delayDays; }

    public String getDelaySeverity() { return delaySeverity; }
    public void setDelaySeverity(String delaySeverity) { this.delaySeverity = delaySeverity; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
}