package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_order_records")
public class PurchaseOrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, length = 50)
    private String poNumber;

    @NotNull
    @Column(nullable = false)
    private Long supplierId;

    @NotBlank
    @Column(length = 500)
    private String itemDescription;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @Column(nullable = false)
    private LocalDate promisedDeliveryDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate issuedDate;

    public PurchaseOrderRecord() {}

    public PurchaseOrderRecord(String poNumber, Long supplierId, String itemDescription, 
                              Integer quantity, LocalDate promisedDeliveryDate, LocalDate issuedDate) {
        this.poNumber = poNumber;
        this.supplierId = supplierId;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.promisedDeliveryDate = promisedDeliveryDate;
        this.issuedDate = issuedDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getPromisedDeliveryDate() { return promisedDeliveryDate; }
    public void setPromisedDeliveryDate(LocalDate promisedDeliveryDate) { this.promisedDeliveryDate = promisedDeliveryDate; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
}