package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Listeners(TestResultListener.class)
public class SupplyChainWeakLinkAnalyzerTest {

    @Mock
    private SupplierProfileRepository supplierProfileRepository;
    @Mock
    private PurchaseOrderRecordRepository poRepository;
    @Mock
    private DeliveryRecordRepository deliveryRepository;
    @Mock
    private DelayScoreRecordRepository delayScoreRecordRepository;
    @Mock
    private SupplierRiskAlertRepository riskAlertRepository;
    @Mock
    private AppUserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private SupplierProfileServiceImpl supplierProfileService;
    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;
    @InjectMocks
    private DeliveryRecordServiceImpl deliveryRecordService;
    @InjectMocks
    private SupplierRiskAlertServiceImpl riskAlertService;

    private DelayScoreServiceImpl delayScoreService;

    @BeforeMethod // Crucial: Use @BeforeMethod to reset mocks for every test
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Explicitly wiring the mocked riskAlertService into delayScoreService
        delayScoreService = new DelayScoreServiceImpl(
                delayScoreRecordRepository,
                poRepository,
                deliveryRepository,
                supplierProfileRepository,
                riskAlertService 
        );

        // Global default: Return the input object on any save call to prevent NPEs
        when(supplierProfileRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(poRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(deliveryRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(riskAlertRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(delayScoreRecordRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    // 1) Servlet behaviors
    @Test(priority = 1)
    public void testControllerLikeResponse_NotNull() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Assert.assertNotNull(supplierProfileService.getSupplierById(1L));
    }

    @Test(priority = 2)
    public void testControllerLikeResponse_404() {
        when(supplierProfileRepository.findById(99L)).thenReturn(Optional.empty());
        Assert.expectThrows(Exception.class, () -> supplierProfileService.getSupplierById(99L));
    }

    @Test(priority = 3)
    public void testSimpleEndpointStyleLogic() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setSupplierCode("SUP-01");
        SupplierProfile created = supplierProfileService.createSupplier(supplier);
        Assert.assertEquals(created.getSupplierCode(), "SUP-01");
    }

    @Test(priority = 5)
    public void testControllerToggleStatus() {
        SupplierProfile s = new SupplierProfile();
        s.setId(10L); s.setActive(true);
        when(supplierProfileRepository.findById(10L)).thenReturn(Optional.of(s));
        SupplierProfile updated = supplierProfileService.updateSupplierStatus(10L, false);
        Assert.assertFalse(updated.getActive());
    }

    // 2) CRUD
    @Test(priority = 14)
    public void testRecordDelivery_success() {
        when(poRepository.existsById(1L)).thenReturn(true);
        DeliveryRecord d = new DeliveryRecord();
        d.setPoId(1L); d.setDeliveredQuantity(5);
        DeliveryRecord saved = deliveryRecordService.recordDelivery(d);
        Assert.assertEquals(saved.getDeliveredQuantity().intValue(), 5);
    }

    @Test(priority = 16)
    public void testRecordDelivery_negativeQuantity() {
        when(poRepository.existsById(1L)).thenReturn(true);
        DeliveryRecord d = new DeliveryRecord();
        d.setPoId(1L); d.setDeliveredQuantity(-1);
        BadRequestException ex = Assert.expectThrows(BadRequestException.class, () -> deliveryRecordService.recordDelivery(d));
        Assert.assertTrue(ex.getMessage().contains("Delivered quantity must be >="));
    }

    // 4) Hibernate/Scoring
    @Test(priority = 23)
    public void testComputeDelayScore_onTime() {
        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setSupplierId(1L); po.setPromisedDeliveryDate(LocalDate.now());
        SupplierProfile s = new SupplierProfile(); s.setActive(true);
        DeliveryRecord d = new DeliveryRecord(); d.setActualDeliveryDate(LocalDate.now());

        when(poRepository.findById(100L)).thenReturn(Optional.of(po));
        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(s));
        when(deliveryRepository.findByPoId(100L)).thenReturn(List.of(d));

        DelayScoreRecord res = delayScoreService.computeDelayScore(100L);
        Assert.assertEquals(res.getDelaySeverity(), "ON_TIME");
    }

    // 6) Relationship tests (Fixed "Expected 2 found 0" issues)
    @Test(priority = 35)
    public void testSupplierMultiplePOsRelationship() {
        when(poRepository.findBySupplierId(1L)).thenReturn(List.of(new PurchaseOrderRecord(), new PurchaseOrderRecord()));
        Assert.assertEquals(purchaseOrderService.getPOsBySupplier(1L).size(), 2);
    }

    @Test(priority = 38)
    public void testSupplierMultipleAlerts() {
        when(riskAlertRepository.findBySupplierId(1L)).thenReturn(List.of(new SupplierRiskAlert(), new SupplierRiskAlert()));
        Assert.assertEquals(riskAlertService.getAlertsBySupplier(1L).size(), 2);
    }

    @Test(priority = 40)
    public void testAlertCreationDefaultResolvedFalse() {
        SupplierRiskAlert a = new SupplierRiskAlert();
        SupplierRiskAlert saved = riskAlertService.createAlert(a);
        Assert.assertFalse(saved.getResolved());
    }

    @Test(priority = 58)
    public void testCriteriaSuppliersActiveOnly() {
        SupplierProfile s1 = new SupplierProfile(); s1.setActive(true);
        SupplierProfile s2 = new SupplierProfile(); s2.setActive(false);
        when(supplierProfileRepository.findAll()).thenReturn(List.of(s1, s2));
        
        List<SupplierProfile> active = supplierProfileService.getAllSuppliers().stream()
                .filter(SupplierProfile::getActive).toList();
        Assert.assertEquals(active.size(), 1);
    }
}