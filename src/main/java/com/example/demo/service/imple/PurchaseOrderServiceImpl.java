package com.example.demo.service.impl;

import org.springframework.stereotype.Service;
import java.util.List;

import com.example.demo.model.PurchaseOrderRecord;
import com.example.demo.repository.PurchaseOrderRecordRepository;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.exception.ResourceNotFoundException;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRecordRepository repo;

    public PurchaseOrderServiceImpl(PurchaseOrderRecordRepository repo) {
        this.repo = repo;
    }

    public PurchaseOrderRecord create(PurchaseOrderRecord po) {
        return repo.save(po);
    }

    public List<PurchaseOrderRecord> getAll() {
        return repo.findAll();
    }

    public PurchaseOrderRecord getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Purchase Order not found with id " + id));
    }

    public PurchaseOrderRecord update(Long id, PurchaseOrderRecord po) {
        PurchaseOrderRecord existing = getById(id);
        existing.orderStatus = po.orderStatus;
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.delete(getById(id));
    }
}