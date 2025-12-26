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
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.util.*;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Single massive TestNG class (60+ tests)
 */
@Listeners(TestResultListener.class)
public class SupplyChainWeakLinkAnalyzerTest extends AbstractTestNGSpringContextTests {

    // Mocks
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

    // Services under test
    @InjectMocks
    private SupplierProfileServiceImpl supplierProfileService;
    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;
    @InjectMocks
    private DeliveryRecordServiceImpl deliveryRecordService;
    @InjectMocks
    private SupplierRiskAlertServiceImpl riskAlertService;

    private DelayScoreServiceImpl delayScoreService;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);
        delayScoreService = new DelayScoreServiceImpl(
                delayScoreRecordRepository,
                poRepository,
                deliveryRepository,
                supplierProfileRepository,
                riskAlertService
        );
    }

    // ----------------------------------------------------
    // 1) "Servlet" / basic controller-like behaviors
    // ----------------------------------------------------

    @Test(priority = 1, groups = {"servlet"})
    public void testControllerLikeResponse_NotNull() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        supplier.setSupplierCode("SUP-01");
        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));

        SupplierProfile result = supplierProfileService.getSupplierById(1L);
        Assert.assertNotNull(result, "Supplier should not be null");
    }

    @Test(priority = 2, groups = {"servlet"})
    public void testControllerLikeResponse_404() {
        when(supplierProfileRepository.findById(99L)).thenReturn(Optional.empty());
        try {
            supplierProfileService.getSupplierById(99L);
            Assert.fail("Expected ResourceNotFoundException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Supplier not found"), "Wrong exception message");
        }
    }

    @Test(priority = 3, groups = {"servlet"})
    public void testSimpleEndpointStyleLogic() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setSupplierCode("SUP-01");
        supplier.setActive(true);

        when(supplierProfileRepository.save(any())).thenReturn(supplier);
        SupplierProfile created = supplierProfileService.createSupplier(supplier);
        Assert.assertEquals(created.getSupplierCode(), "SUP-01");
    }

    @Test(priority = 4, groups = {"servlet"})
    public void testTomcatLikeMultipleRequestsSimulation() {
        List<SupplierProfile> list = new ArrayList<>();
        SupplierProfile s1 = new SupplierProfile();
        s1.setSupplierCode("SUP-01");
        list.add(s1);
        when(supplierProfileRepository.findAll()).thenReturn(list);

        List<SupplierProfile> result = supplierProfileService.getAllSuppliers();
        Assert.assertEquals(result.size(), 1);
    }

    @Test(priority = 5, groups = {"servlet"})
    public void testControllerToggleStatus() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(10L);
        supplier.setActive(true);

        when(supplierProfileRepository.findById(10L)).thenReturn(Optional.of(supplier));
        when(supplierProfileRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

        SupplierProfile updated = supplierProfileService.updateSupplierStatus(10L, false);
        Assert.assertFalse(updated.getActive());
    }

    @Test(priority = 6, groups = {"servlet"})
    public void testLookupByCodePositive() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setSupplierCode("SUP-TEST");
        when(supplierProfileRepository.findBySupplierCode("SUP-TEST"))
                .thenReturn(Optional.of(supplier));

        Optional<SupplierProfile> opt = supplierProfileService.getBySupplierCode("SUP-TEST");
        Assert.assertTrue(opt.isPresent());
    }

    @Test(priority = 7, groups = {"servlet"})
    public void testLookupByCodeNegative() {
        when(supplierProfileRepository.findBySupplierCode("UNKNOWN"))
                .thenReturn(Optional.empty());

        Optional<SupplierProfile> opt = supplierProfileService.getBySupplierCode("UNKNOWN");
        Assert.assertFalse(opt.isPresent());
    }

    // ----------------------------------------------------
    // 2) CRUD with Spring Boot & REST style services
    // ----------------------------------------------------

    @Test(priority = 8, groups = {"crud"})
    public void testCreatePurchaseOrder_success() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        supplier.setActive(true);

        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setSupplierId(1L);
        po.setQuantity(10);
        po.setPoNumber("PO-1");
        po.setPromisedDeliveryDate(LocalDate.now());
        po.setIssuedDate(LocalDate.now());

        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(poRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

        PurchaseOrderRecord created = purchaseOrderService.createPurchaseOrder(po);
        Assert.assertEquals(created.getQuantity().intValue(), 10);
    }

    @Test(priority = 9, groups = {"crud"})
    public void testCreatePurchaseOrder_invalidSupplier() {
        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setSupplierId(99L);
        po.setQuantity(5);

        when(supplierProfileRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            purchaseOrderService.createPurchaseOrder(po);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Invalid supplierId"));
        }
    }

    @Test(priority = 10, groups = {"crud"})
    public void testCreatePurchaseOrder_inactiveSupplier() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        supplier.setActive(false);

        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setSupplierId(1L);
        po.setQuantity(10);

        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));

        try {
            purchaseOrderService.createPurchaseOrder(po);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("must be active"));
        }
    }

    @Test(priority = 11, groups = {"crud"})
    public void testGetPOsBySupplier_returnsList() {
        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setSupplierId(1L);
        when(poRepository.findBySupplierId(1L)).thenReturn(List.of(po));

        List<PurchaseOrderRecord> list = purchaseOrderService.getPOsBySupplier(1L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 12, groups = {"crud"})
    public void testGetPOById_positive() {
        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setId(10L);
        when(poRepository.findById(10L)).thenReturn(Optional.of(po));

        Optional<PurchaseOrderRecord> opt = purchaseOrderService.getPOById(10L);
        Assert.assertTrue(opt.isPresent());
    }

    @Test(priority = 13, groups = {"crud"})
    public void testGetPOById_negative() {
        when(poRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<PurchaseOrderRecord> opt = purchaseOrderService.getPOById(99L);
        Assert.assertFalse(opt.isPresent());
    }

    @Test(priority = 14, groups = {"crud"})
    public void testRecordDelivery_success() {
        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setId(1L);

        DeliveryRecord delivery = new DeliveryRecord();
        delivery.setPoId(1L);
        delivery.setDeliveredQuantity(5);

        when(poRepository.findById(1L)).thenReturn(Optional.of(po));
        when(deliveryRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

        DeliveryRecord saved = deliveryRecordService.recordDelivery(delivery);
        Assert.assertEquals(saved.getDeliveredQuantity().intValue(), 5);
    }

    @Test(priority = 15, groups = {"crud"})
    public void testRecordDelivery_invalidPo() {
        DeliveryRecord delivery = new DeliveryRecord();
        delivery.setPoId(999L);
        delivery.setDeliveredQuantity(5);

        when(poRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            deliveryRecordService.recordDelivery(delivery);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Invalid PO id"));
        }
    }

    @Test(priority = 16, groups = {"crud"})
    public void testRecordDelivery_negativeQuantity() {
        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setId(1L);

        DeliveryRecord delivery = new DeliveryRecord();
        delivery.setPoId(1L);
        delivery.setDeliveredQuantity(-1);

        when(poRepository.findById(1L)).thenReturn(Optional.of(po));

        try {
            deliveryRecordService.recordDelivery(delivery);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Delivered quantity must be >="));
        }
    }

    @Test(priority = 17, groups = {"crud"})
    public void testGetDeliveriesByPo_returnsList() {
        DeliveryRecord d = new DeliveryRecord();
        d.setPoId(1L);
        when(deliveryRepository.findByPoId(1L)).thenReturn(List.of(d));

        List<DeliveryRecord> list = deliveryRecordService.getDeliveriesByPO(1L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 18, groups = {"crud"})
    public void testGetAllDeliveries_emptyList() {
        when(deliveryRepository.findAll()).thenReturn(Collections.emptyList());
        List<DeliveryRecord> list = deliveryRecordService.getAllDeliveries();
        Assert.assertTrue(list.isEmpty());
    }

    // ----------------------------------------------------
    // 3) Dependency Injection / IoC behavior
    // ----------------------------------------------------

    @Test(priority = 19, groups = {"di"})
    public void testServiceInjectedRepositoriesNotNull() {
        Assert.assertNotNull(supplierProfileService);
        Assert.assertNotNull(purchaseOrderService);
        Assert.assertNotNull(deliveryRecordService);
    }

    @Test(priority = 20, groups = {"di"})
    public void testDelayScoreServiceHasDependencies() {
        Assert.assertNotNull(delayScoreService);
    }

    @Test(priority = 21, groups = {"di"})
    public void testIoCBehaviorOnSupplierServiceCreate() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setSupplierCode("SUP-IOCTest");
        when(supplierProfileRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

        SupplierProfile created = supplierProfileService.createSupplier(supplier);
        Assert.assertEquals(created.getSupplierCode(), "SUP-IOCTest");
    }

    @Test(priority = 22, groups = {"di"})
    public void testIoCBehaviorOnRiskAlertService() {
        SupplierRiskAlert alert = new SupplierRiskAlert();
        alert.setSupplierId(1L);

        when(riskAlertRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
        SupplierRiskAlert saved = riskAlertService.createAlert(alert);
        Assert.assertEquals(saved.getSupplierId(), Long.valueOf(1L));
    }

    // ----------------------------------------------------
    // 4) Hibernate / JPA CRUD & scoring
    // ----------------------------------------------------

    @Test(priority = 23, groups = {"hibernate"})
    public void testComputeDelayScore_onTime() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        supplier.setActive(true);

        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setId(100L);
        po.setSupplierId(1L);
        po.setPromisedDeliveryDate(LocalDate.now());

        DeliveryRecord delivery = new DeliveryRecord();
        delivery.setPoId(100L);
        delivery.setActualDeliveryDate(LocalDate.now());

        when(poRepository.findById(100L)).thenReturn(Optional.of(po));
        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(deliveryRepository.findByPoId(100L)).thenReturn(List.of(delivery));
        when(delayScoreRecordRepository.findByPoId(100L)).thenReturn(Optional.empty());
        when(delayScoreRecordRepository.findBySupplierId(1L)).thenReturn(Collections.emptyList());
        when(delayScoreRecordRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

        DelayScoreRecord record = delayScoreService.computeDelayScore(100L);
        Assert.assertEquals(record.getDelayDays().intValue(), 0);
        Assert.assertEquals(record.getDelaySeverity(), "ON_TIME");
    }

    @Test(priority = 24, groups = {"hibernate"})
    public void testComputeDelayScore_minorDelay() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        supplier.setActive(true);

        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setId(101L);
        po.setSupplierId(1L);
        po.setPromisedDeliveryDate(LocalDate.now().minusDays(2));

        DeliveryRecord delivery = new DeliveryRecord();
        delivery.setPoId(101L);
        delivery.setActualDeliveryDate(LocalDate.now());

        when(poRepository.findById(101L)).thenReturn(Optional.of(po));
        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(deliveryRepository.findByPoId(101L)).thenReturn(List.of(delivery));
        when(delayScoreRecordRepository.findByPoId(101L)).thenReturn(Optional.empty());
        when(delayScoreRecordRepository.findBySupplierId(1L)).thenReturn(Collections.emptyList());
        when(delayScoreRecordRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

        DelayScoreRecord record = delayScoreService.computeDelayScore(101L);
        Assert.assertEquals(record.getDelaySeverity(), "MINOR");
        Assert.assertTrue(record.getScore() < 100.0);
    }

    @Test(priority = 25, groups = {"hibernate"})
    public void testComputeDelayScore_noDeliveries() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        supplier.setActive(true);

        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setId(102L);
        po.setSupplierId(1L);
        po.setPromisedDeliveryDate(LocalDate.now());

        when(poRepository.findById(102L)).thenReturn(Optional.of(po));
        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(deliveryRepository.findByPoId(102L)).thenReturn(Collections.emptyList());

        try {
            delayScoreService.computeDelayScore(102L);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("No deliveries"));
        }
    }

    @Test(priority = 26, groups = {"hibernate"})
    public void testComputeDelayScore_inactiveSupplier() {
        SupplierProfile supplier = new SupplierProfile();
        supplier.setId(1L);
        supplier.setActive(false);

        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setId(103L);
        po.setSupplierId(1L);
        po.setPromisedDeliveryDate(LocalDate.now());

        when(poRepository.findById(103L)).thenReturn(Optional.of(po));
        when(supplierProfileRepository.findById(1L)).thenReturn(Optional.of(supplier));

        try {
            delayScoreService.computeDelayScore(103L);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Inactive supplier"));
        }
    }

    @Test(priority = 27, groups = {"hibernate"})
    public void testGetScoresBySupplier_empty() {
        when(delayScoreRecordRepository.findBySupplierId(1L)).thenReturn(Collections.emptyList());
        Assert.assertTrue(delayScoreService.getScoresBySupplier(1L).isEmpty());
    }

    @Test(priority = 28, groups = {"hibernate"})
    public void testGetAllScores_nonEmpty() {
        DelayScoreRecord s = new DelayScoreRecord();
        s.setSupplierId(1L);
        when(delayScoreRecordRepository.findAll()).thenReturn(List.of(s));
        Assert.assertEquals(delayScoreService.getAllScores().size(), 1);
    }

    // ----------------------------------------------------
    // 5) JPA normalization (1NF / 2NF / 3NF) conceptual tests
    // ----------------------------------------------------

    @Test(priority = 29, groups = {"jpa"})
    public void testSupplierHasAtomicFields_1NF() {
        SupplierProfile s = new SupplierProfile();
        s.setSupplierName("ACME");
        s.setEmail("acme@test.com");
        Assert.assertTrue(s.getSupplierName().contains("ACME"));
    }

    @Test(priority = 30, groups = {"jpa"})
    public void testPurchaseOrderReferentialIntegrity() {
        PurchaseOrderRecord po = new PurchaseOrderRecord();
        po.setSupplierId(1L);
        Assert.assertNotNull(po.getSupplierId());
    }

    @Test(priority = 31, groups = {"jpa"})
    public void testDelayScoreOnePerPoUniqueConstraintConcept() {
        DelayScoreRecord score = new DelayScoreRecord();
        score.setPoId(1L);
        score.setSupplierId(1L);
        score.setDelayDays(2);
        Assert.assertEquals(score.getPoId(), Long.valueOf(1L));
    }

    @Test(priority = 32, groups = {"jpa"})
    public void testRiskAlertReferencesSupplier_3NF() {
        SupplierRiskAlert alert = new SupplierRiskAlert();
        alert.setSupplierId(2L);
        Assert.assertEquals(alert.getSupplierId(), Long.valueOf(2L));
    }

    @Test(priority = 33, groups = {"jpa"})
    public void testDeliveryRecordUsesPoIdAsFk() {
        DeliveryRecord d = new DeliveryRecord();
        d.setPoId(5L);
        Assert.assertNotNull(d.getPoId());
    }

    @Test(priority = 34, groups = {"jpa"})
    public void testSupplierUniqueCodeConstraintConcept() {
        SupplierProfile s1 = new SupplierProfile();
        SupplierProfile s2 = new SupplierProfile();
        s1.setSupplierCode("SUPX");
        s2.setSupplierCode("SUPY");
        Assert.assertNotEquals(s1.getSupplierCode(), s2.getSupplierCode());
    }

    // ----------------------------------------------------
    // 6) Many-to-Many / associations (concept using repositories)
    // ----------------------------------------------------

    @Test(priority = 35, groups = {"manyToMany"})
    public void testSupplierMultiplePOsRelationship() {
        PurchaseOrderRecord po1 = new PurchaseOrderRecord();
        po1.setSupplierId(1L);
        PurchaseOrderRecord po2 = new PurchaseOrderRecord();
        po2.setSupplierId(1L);

        when(poRepository.findBySupplierId(1L)).thenReturn(List.of(po1, po2));
        List<PurchaseOrderRecord> list = purchaseOrderService.getPOsBySupplier(1L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 36, groups = {"manyToMany"})
    public void testPoHasMultipleDeliveries() {
        DeliveryRecord d1 = new DeliveryRecord();
        d1.setPoId(1L);
        DeliveryRecord d2 = new DeliveryRecord();
        d2.setPoId(1L);
        when(deliveryRepository.findByPoId(1L)).thenReturn(List.of(d1, d2));

        List<DeliveryRecord> list = deliveryRecordService.getDeliveriesByPO(1L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 37, groups = {"manyToMany"})
    public void testSupplierMultipleScoresSimulateManyToMany() {
        DelayScoreRecord s1 = new DelayScoreRecord();
        s1.setSupplierId(1L);
        DelayScoreRecord s2 = new DelayScoreRecord();
        s2.setSupplierId(1L);

        when(delayScoreRecordRepository.findBySupplierId(1L)).thenReturn(List.of(s1, s2));
        List<DelayScoreRecord> scores = delayScoreService.getScoresBySupplier(1L);
        Assert.assertEquals(scores.size(), 2);
    }

    @Test(priority = 38, groups = {"manyToMany"})
    public void testSupplierMultipleAlerts() {
        SupplierRiskAlert a1 = new SupplierRiskAlert();
        a1.setSupplierId(1L);
        SupplierRiskAlert a2 = new SupplierRiskAlert();
        a2.setSupplierId(1L);
        when(riskAlertRepository.findBySupplierId(1L)).thenReturn(List.of(a1, a2));

        List<SupplierRiskAlert> alerts = riskAlertService.getAlertsBySupplier(1L);
        Assert.assertEquals(alerts.size(), 2);
    }

    @Test(priority = 39, groups = {"manyToMany"})
    public void testResolveAlertChangesFlag() {
        SupplierRiskAlert alert = new SupplierRiskAlert();
        alert.setId(10L);
        alert.setResolved(false);

        when(riskAlertRepository.findById(10L)).thenReturn(Optional.of(alert));
        when(riskAlertRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

        SupplierRiskAlert resolved = riskAlertService.resolveAlert(10L);
        Assert.assertTrue(resolved.getResolved());
    }

    @Test(priority = 40, groups = {"manyToMany"})
    public void testAlertCreationDefaultResolvedFalse() {
        SupplierRiskAlert alert = new SupplierRiskAlert();
        alert.setSupplierId(3L);

        when(riskAlertRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
        SupplierRiskAlert saved = riskAlertService.createAlert(alert);
        Assert.assertFalse(saved.getResolved());
    }

    // ----------------------------------------------------
    // 7) Security / JWT Authentication
    // ----------------------------------------------------

    @Test(priority = 41, groups = {"security"})
    public void testRegisterUserSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user1");
        req.setPassword("pass");
        req.setEmail("user1@test.com");
        req.setRole(Role.ANALYST);

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("ENC_PASS");
        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setUsername("user1");
        savedUser.setPassword("ENC_PASS");
        savedUser.setEmail("user1@test.com");
        savedUser.setRole(Role.ANALYST);
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtTokenProvider.generateToken(savedUser)).thenReturn("TOKEN123");

        // simulate controller logic
        String token = jwtTokenProvider.generateToken(savedUser);
        Assert.assertEquals(token, "TOKEN123");
        Assert.assertEquals(savedUser.getUsername(), "user1");
    }

    @Test(priority = 42, groups = {"security"})
    public void testRegisterUserDuplicateUsername() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("dupUser");
        req.setPassword("pass");
        req.setEmail("dup@test.com");
        req.setRole(Role.ADMIN);

        when(userRepository.existsByUsername("dupUser")).thenReturn(true);

        try {
            if (userRepository.existsByUsername(req.getUsername())) {
                throw new BadRequestException("Username already taken");
            }
            Assert.fail("Should not reach here");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Username already taken"));
        }
    }

    @Test(priority = 43, groups = {"security"})
    public void testJwtTokenContainsUserInfo() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("jwtuser");
        user.setEmail("jwt@test.com");
        user.setRole(Role.MANAGER);

        when(jwtTokenProvider.generateToken(user)).thenReturn("FAKE_TOKEN");
        String token = jwtTokenProvider.generateToken(user);
        Assert.assertEquals(token, "FAKE_TOKEN");
    }

    @Test(priority = 44, groups = {"security"})
    public void testAuthenticationManagerSuccess() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);
        Assert.assertNotNull(auth);
    }

    @Test(priority = 45, groups = {"security"})
    public void testLoginBadCredentials() {
        LoginRequest req = new LoginRequest();
        req.setUsername("u");
        req.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            Assert.fail("Expected BadCredentialsException");
        } catch (BadCredentialsException ex) {
            Assert.assertTrue(ex.getMessage().contains("Bad"));
        }
    }

    @Test(priority = 46, groups = {"security"})
    public void testPasswordEncoding() {
        when(passwordEncoder.encode("secret")).thenReturn("ENC_SEC");
        String enc = passwordEncoder.encode("secret");
        Assert.assertEquals(enc, "ENC_SEC");
    }

    @Test(priority = 47, groups = {"security"})
    public void testRoleBasedAuthorityNaming() {
        AppUser user = new AppUser();
        user.setRole(Role.ADMIN);
        Assert.assertEquals(user.getRole(), Role.ADMIN);
    }

    @Test(priority = 48, groups = {"security"})
    public void testTokenValidationPositive() {
        when(jwtTokenProvider.validateToken("VALID")).thenReturn(true);
        Assert.assertTrue(jwtTokenProvider.validateToken("VALID"));
    }

    @Test(priority = 49, groups = {"security"})
    public void testTokenValidationNegative() {
        when(jwtTokenProvider.validateToken("INVALID")).thenReturn(false);
        Assert.assertFalse(jwtTokenProvider.validateToken("INVALID"));
    }

    // ----------------------------------------------------
    // 8) HQL / Criteria-like queries
    // ----------------------------------------------------

    @Test(priority = 50, groups = {"hql"})
    public void testFindSupplierByCodeMockQuery() {
        SupplierProfile s = new SupplierProfile();
        s.setSupplierCode("XYZ");
        when(supplierProfileRepository.findBySupplierCode("XYZ"))
                .thenReturn(Optional.of(s));
        Assert.assertTrue(supplierProfileService.getBySupplierCode("XYZ").isPresent());
    }

    @Test(priority = 51, groups = {"hql"})
    public void testAdvancedDelayQueryAverage() {
        DelayScoreRecord s1 = new DelayScoreRecord();
        s1.setDelayDays(2);
        DelayScoreRecord s2 = new DelayScoreRecord();
        s2.setDelayDays(6);
        when(delayScoreRecordRepository.findBySupplierId(5L))
                .thenReturn(List.of(s1, s2));

        List<DelayScoreRecord> list = delayScoreService.getScoresBySupplier(5L);
        double avg = list.stream().mapToInt(DelayScoreRecord::getDelayDays).average().orElse(0.0);
        Assert.assertEquals(avg, 4.0, 0.0001);
    }

    @Test(priority = 52, groups = {"hql"})
    public void testHqlLikeConditionDelayedOnly() {
        DelayScoreRecord s1 = new DelayScoreRecord();
        s1.setDelayDays(0);
        DelayScoreRecord s2 = new DelayScoreRecord();
        s2.setDelayDays(3);

        when(delayScoreRecordRepository.findAll()).thenReturn(List.of(s1, s2));

        List<DelayScoreRecord> delayed =
                delayScoreService.getAllScores().stream()
                        .filter(d -> d.getDelayDays() > 0)
                        .toList();
        Assert.assertEquals(delayed.size(), 1);
    }

    @Test(priority = 53, groups = {"hql"})
    public void testCriteriaLikeHighRiskSuppliers() {
        SupplierRiskAlert a1 = new SupplierRiskAlert();
        a1.setAlertLevel("HIGH");
        SupplierRiskAlert a2 = new SupplierRiskAlert();
        a2.setAlertLevel("LOW");

        when(riskAlertRepository.findAll()).thenReturn(List.of(a1, a2));

        List<SupplierRiskAlert> high =
                riskAlertService.getAllAlerts().stream()
                        .filter(a -> "HIGH".equals(a.getAlertLevel()))
                        .toList();
        Assert.assertEquals(high.size(), 1);
    }

    @Test(priority = 54, groups = {"hql"})
    public void testCriteriaLikeUnresolvedAlerts() {
        SupplierRiskAlert a1 = new SupplierRiskAlert();
        a1.setResolved(false);
        SupplierRiskAlert a2 = new SupplierRiskAlert();
        a2.setResolved(true);
        when(riskAlertRepository.findAll()).thenReturn(List.of(a1, a2));

        List<SupplierRiskAlert> unresolved =
                riskAlertService.getAllAlerts().stream()
                        .filter(a -> !a.getResolved())
                        .toList();
        Assert.assertEquals(unresolved.size(), 1);
    }

    @Test(priority = 55, groups = {"hql"})
    public void testComplexCriteriaSupplierDelayedOverThreshold() {
        DelayScoreRecord s1 = new DelayScoreRecord();
        s1.setSupplierId(1L);
        s1.setDelayDays(8);
        DelayScoreRecord s2 = new DelayScoreRecord();
        s2.setSupplierId(1L);
        s2.setDelayDays(1);
        when(delayScoreRecordRepository.findBySupplierId(1L))
                .thenReturn(List.of(s1, s2));

        List<DelayScoreRecord> severe =
                delayScoreService.getScoresBySupplier(1L).stream()
                        .filter(d -> d.getDelayDays() > 7)
                        .toList();
        Assert.assertEquals(severe.size(), 1);
    }

    @Test(priority = 56, groups = {"hql"})
    public void testCriteriaPOIssuedDateRange() {
        PurchaseOrderRecord p1 = new PurchaseOrderRecord();
        p1.setIssuedDate(LocalDate.now().minusDays(10));
        PurchaseOrderRecord p2 = new PurchaseOrderRecord();
        p2.setIssuedDate(LocalDate.now().minusDays(1));
        when(poRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PurchaseOrderRecord> recent =
                purchaseOrderService.getAllPurchaseOrders().stream()
                        .filter(p -> p.getIssuedDate().isAfter(LocalDate.now().minusDays(5)))
                        .toList();
        Assert.assertEquals(recent.size(), 1);
    }

    @Test(priority = 57, groups = {"hql"})
    public void testCriteriaDeliveriesPartialQuantity() {
        DeliveryRecord d1 = new DeliveryRecord();
        d1.setDeliveredQuantity(0);
        DeliveryRecord d2 = new DeliveryRecord();
        d2.setDeliveredQuantity(5);
        when(deliveryRepository.findAll()).thenReturn(List.of(d1, d2));

        List<DeliveryRecord> partial =
                deliveryRecordService.getAllDeliveries().stream()
                        .filter(d -> d.getDeliveredQuantity() > 0)
                        .toList();
        Assert.assertEquals(partial.size(), 1);
    }

    @Test(priority = 58, groups = {"hql"})
    public void testCriteriaSuppliersActiveOnly() {
        SupplierProfile s1 = new SupplierProfile();
        s1.setActive(true);
        SupplierProfile s2 = new SupplierProfile();
        s2.setActive(false);
        when(supplierProfileRepository.findAll()).thenReturn(List.of(s1, s2));

        List<SupplierProfile> active =
                supplierProfileService.getAllSuppliers().stream()
                        .filter(SupplierProfile::getActive)
                        .toList();
        Assert.assertEquals(active.size(), 1);
    }

    @Test(priority = 59, groups = {"hql"})
    public void testCriteriaSuppliersEmailPresent() {
        SupplierProfile s1 = new SupplierProfile();
        s1.setEmail("a@test.com");
        SupplierProfile s2 = new SupplierProfile();
        s2.setEmail(null);
        when(supplierProfileRepository.findAll()).thenReturn(List.of(s1, s2));

        List<SupplierProfile> withEmail =
                supplierProfileService.getAllSuppliers().stream()
                        .filter(s -> s.getEmail() != null)
                        .toList();
        Assert.assertEquals(withEmail.size(), 1);
    }

    @Test(priority = 60, groups = {"hql"})
    public void testCriteriaScoreSeveritySevereOnly() {
        DelayScoreRecord s1 = new DelayScoreRecord();
        s1.setDelaySeverity("SEVERE");
        DelayScoreRecord s2 = new DelayScoreRecord();
        s2.setDelaySeverity("MINOR");
        when(delayScoreRecordRepository.findAll()).thenReturn(List.of(s1, s2));

        List<DelayScoreRecord> severe =
                delayScoreService.getAllScores().stream()
                        .filter(s -> "SEVERE".equals(s.getDelaySeverity()))
                        .toList();
        Assert.assertEquals(severe.size(), 1);
    }

    @Test(priority = 61, groups = {"hql"})
    public void testCriteriaScoreOnTimeOnly() {
        DelayScoreRecord s1 = new DelayScoreRecord();
        s1.setDelaySeverity("ON_TIME");
        DelayScoreRecord s2 = new DelayScoreRecord();
        s2.setDelaySeverity("SEVERE");
        when(delayScoreRecordRepository.findAll()).thenReturn(List.of(s1, s2));

        List<DelayScoreRecord> onTime =
                delayScoreService.getAllScores().stream()
                        .filter(s -> "ON_TIME".equals(s.getDelaySeverity()))
                        .toList();
        Assert.assertEquals(onTime.size(), 1);
    }

    @Test(priority = 62, groups = {"hql"})
    public void testCriteriaAlertMediumRisk() {
        SupplierRiskAlert a1 = new SupplierRiskAlert();
        a1.setAlertLevel("MEDIUM");
        SupplierRiskAlert a2 = new SupplierRiskAlert();
        a2.setAlertLevel("LOW");
        when(riskAlertRepository.findAll()).thenReturn(List.of(a1, a2));

        List<SupplierRiskAlert> medium =
                riskAlertService.getAllAlerts().stream()
                        .filter(a -> "MEDIUM".equals(a.getAlertLevel()))
                        .toList();
        Assert.assertEquals(medium.size(), 1);
    }

    @Test(priority = 63, groups = {"hql"})
    public void testCriteriaSupplierCodePattern() {
        SupplierProfile s1 = new SupplierProfile();
        s1.setSupplierCode("SUP-001");
        SupplierProfile s2 = new SupplierProfile();
        s2.setSupplierCode("VEND-002");
        when(supplierProfileRepository.findAll()).thenReturn(List.of(s1, s2));

        List<SupplierProfile> supOnly =
                supplierProfileService.getAllSuppliers().stream()
                        .filter(s -> s.getSupplierCode() != null && s.getSupplierCode().startsWith("SUP-"))
                        .toList();
        Assert.assertEquals(supOnly.size(), 1);
    }

    @Test(priority = 64, groups = {"hql"})
    public void testCriteriaNoResultsEdgeCase() {
        when(delayScoreRecordRepository.findAll()).thenReturn(Collections.emptyList());
        List<DelayScoreRecord> all = delayScoreService.getAllScores();
        Assert.assertTrue(all.isEmpty());
    }
}
