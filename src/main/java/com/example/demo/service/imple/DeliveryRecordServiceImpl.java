package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.DeliveryRecord;
import com.example.demo.repository.DeliveryRecordRepository;
import com.example.demo.repository.PurchaseOrderRecordRepository;
import com.example.demo.service.DeliveryRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryRecordServiceImpl implements DeliveryRecordService {

    private final DeliveryRecordRepository deliveryRepository;
    private final PurchaseOrderRecordRepository poRepository;

    public DeliveryRecordServiceImpl(DeliveryRecordRepository deliveryRepository,
                                    PurchaseOrderRecordRepository poRepository) {
        this.deliveryRepository = deliveryRepository;
        this.poRepository = poRepository;
    }

    @Override
    public DeliveryRecord recordDelivery(DeliveryRecord delivery) {
        if (!poRepository.existsById(delivery.getPoId())) {
            throw new BadRequestException("Invalid PO id");
        }
        
        if (delivery.getQuantityDelivered() < 0) {
            throw new BadRequestException("Delivered quantity must be >=");
        }
        
        return deliveryRepository.save(delivery);
    }

    @Override
    public List<DeliveryRecord> getDeliveriesByPoId(Long poId) {
        return deliveryRepository.findByPoId(poId);
    }

    @Override
    public List<DeliveryRecord> getAllDeliveries() {
        return deliveryRepository.findAll();
    }
}