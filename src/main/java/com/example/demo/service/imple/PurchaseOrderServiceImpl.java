package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.PurchaseOrderRecord;
import com.example.demo.model.SupplierProfile;
import com.example.demo.repository.PurchaseOrderRecordRepository;
import com.example.demo.repository.SupplierProfileRepository;
import com.example.demo.service.PurchaseOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRecordRepository poRepository;
    private final SupplierProfileRepository supplierRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRecordRepository poRepository, 
                                   SupplierProfileRepository supplierRepository) {
        this.poRepository = poRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public PurchaseOrderRecord createPurchaseOrder(PurchaseOrderRecord po) {
        SupplierProfile supplier = supplierRepository.findById(po.getSupplierId())
                .orElseThrow(() -> new BadRequestException("Invalid supplierId"));
        
        if (!supplier.getActive()) {
            throw new BadRequestException("must be active");
        }
        
        if (po.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        
        return poRepository.save(po);
    }

    public List<PurchaseOrderRecord> getPOsBySupplierId(Long supplierId) {
        return poRepository.findBySupplierId(supplierId);
    }

    public List<PurchaseOrderRecord> getPOsBySupplier(Long supplierId) {
        return poRepository.findBySupplierId(supplierId);
    }

    @Override
    public Optional<PurchaseOrderRecord> getPOById(Long id) {
        return poRepository.findById(id);
    }

    @Override
    public List<PurchaseOrderRecord> getAllPurchaseOrders() {
        return poRepository.findAll();
    }
}