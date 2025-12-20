package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "delivery_records")
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long poId;

    @NotNull
    @Column(nullable = false)
    private LocalDate actualDeliveryDate;

    @NotNull
    @Column(nullable = false)
    private Integer deliveredQuantity;

    @Column(length = 500)
    private String notes;

    public DeliveryRecord() {}

    public DeliveryRecord(Long poId, LocalDate actualDeliveryDate, Integer deliveredQuantity, String notes) {
        this.poId = poId;
        this.actualDeliveryDate = actualDeliveryDate;
        this.deliveredQuantity = deliveredQuantity;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPoId() { return poId; }
    public void setPoId(Long poId) { this.poId = poId; }

    public LocalDate getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDate actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public Integer getDeliveredQuantity() { return deliveredQuantity; }
    public void setDeliveredQuantity(Integer deliveredQuantity) { this.deliveredQuantity = deliveredQuantity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}