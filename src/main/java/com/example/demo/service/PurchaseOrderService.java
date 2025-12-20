package com.example.demo.service;

import com.example.demo.model.PurchaseOrderRecord;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderRecord create(PurchaseOrderRecord po);
    List<PurchaseOrderRecord> getAll();
    PurchaseOrderRecord getById(Long id);
    PurchaseOrderRecord update(Long id, PurchaseOrderRecord po);
    void delete(Long id);
}