package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.repository.DelayScoreRecordRepository;
import com.example.demo.repository.DeliveryRecordRepository;
import com.example.demo.repository.PurchaseOrderRecordRepository;
import com.example.demo.repository.SupplierProfileRepository;
import com.example.demo.service.impl.DelayScoreServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SupplyChainWeakLinkAnalyzerTest {

    @Mock
    private DelayScoreRecordRepository delayScoreRepository;
    
    @Mock
    private PurchaseOrderRecordRepository poRepository;
    
    @Mock
    private DeliveryRecordRepository deliveryRepository;
    
    @Mock
    private SupplierProfileRepository supplierRepository;

    @Test
    public void testDelayScoreServiceCreation() {
        DelayScoreServiceImpl service = new DelayScoreServiceImpl(
            delayScoreRepository,
            poRepository,
            deliveryRepository,
            supplierRepository
        );
        assertNotNull(service);
    }

    @Test
    public void testLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        assertNotNull(req.getUsername());
    }
}