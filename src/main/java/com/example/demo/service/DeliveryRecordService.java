package com.example.demo.service;

import com.example.demo.model.DeliveryRecord;

import java.util.List;

public interface DeliveryRecordService {
    DeliveryRecord recordDelivery(DeliveryRecord delivery);
    List<DeliveryRecord> getDeliveriesByPO(Long poId);
    List<DeliveryRecord> getAllDeliveries();
}