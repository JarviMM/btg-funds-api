package com.btg.funds.service;

import com.btg.funds.dto.TransactionResponse;
import com.btg.funds.entity.*;
import com.btg.funds.exception.*;
import com.btg.funds.notification.NotificationFactory;
import com.btg.funds.notification.NotificationService;
import com.btg.funds.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundServiceTest {

    @Mock private FundRepository fundRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private NotificationFactory notificationFactory;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private FundService fundService;

    private Client testClient;
    private Fund testFund;

    @BeforeEach
    void setUp() {
        testClient = new Client("Juan Pérez", "juan@email.com", "+573001234567",
                500000.0, Client.NotificationType.EMAIL);
        testClient.setId(1L);

        testFund = new Fund("FPV_BTG_PACTUAL_RECAUDADORA", 75000.0, "FPV");
        testFund.setId(1L);
    }

    @Test
    @DisplayName("Suscripción exitosa a un fondo")
    void subscribe_success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(fundRepository.findById(1L)).thenReturn(Optional.of(testFund));
        when(subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(1L, 1L)).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(notificationFactory.getService(Client.NotificationType.EMAIL)).thenReturn(notificationService);

        Transaction result = fundService.subscribe(1L, 1L);

        assertNotNull(result);
        assertEquals(Transaction.TransactionType.APERTURA, result.getType());
        assertEquals(75000.0, result.getAmount());
        assertEquals(425000.0, testClient.getBalance());
        verify(notificationService).sendSubscriptionNotification(testClient, "FPV_BTG_PACTUAL_RECAUDADORA", 75000.0);
    }

    @Test
    @DisplayName("Suscripción falla por saldo insuficiente")
    void subscribe_insufficientBalance() {
        testClient.setBalance(50000.0);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(fundRepository.findById(1L)).thenReturn(Optional.of(testFund));
        when(subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(1L, 1L)).thenReturn(Optional.empty());

        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class,
                () -> fundService.subscribe(1L, 1L));

        assertTrue(ex.getMessage().contains("FPV_BTG_PACTUAL_RECAUDADORA"));
        assertEquals(50000.0, testClient.getBalance());
    }

    @Test
    @DisplayName("Suscripción falla por suscripción duplicada")
    void subscribe_duplicate() {
        Subscription existing = new Subscription(testClient, testFund, 75000.0);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(fundRepository.findById(1L)).thenReturn(Optional.of(testFund));
        when(subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(existing));

        assertThrows(DuplicateSubscriptionException.class,
                () -> fundService.subscribe(1L, 1L));
    }

    @Test
    @DisplayName("Suscripción falla por cliente no encontrado")
    void subscribe_clientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> fundService.subscribe(99L, 1L));
    }

    @Test
    @DisplayName("Cancelación exitosa de suscripción")
    void cancelSubscription_success() {
        Subscription subscription = new Subscription(testClient, testFund, 75000.0);
        testClient.setBalance(425000.0);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(fundRepository.findById(1L)).thenReturn(Optional.of(testFund));
        when(subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(subscription));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = fundService.cancelSubscription(1L, 1L);

        assertNotNull(result);
        assertEquals(Transaction.TransactionType.CANCELACION, result.getType());
        assertEquals(75000.0, result.getAmount());
        assertEquals(500000.0, testClient.getBalance());
        assertFalse(subscription.getActive());
    }

    @Test
    @DisplayName("Cancelación falla por suscripción no encontrada")
    void cancelSubscription_notFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(fundRepository.findById(1L)).thenReturn(Optional.of(testFund));
        when(subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> fundService.cancelSubscription(1L, 1L));
    }

    @Test
    @DisplayName("Obtener historial de transacciones")
    void getTransactionHistory_success() {
        Transaction tx = new Transaction(testClient, testFund, Transaction.TransactionType.APERTURA, 75000.0);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(transactionRepository.findByClientIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(tx));

        List<TransactionResponse> result = fundService.getTransactionHistory(1L);

        assertEquals(1, result.size());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", result.get(0).getFundName());
        assertEquals("APERTURA", result.get(0).getType());
    }

    @Test
    @DisplayName("Obtener todos los fondos")
    void getAllFunds() {
        when(fundRepository.findAll()).thenReturn(List.of(testFund));

        List<Fund> result = fundService.getAllFunds();

        assertEquals(1, result.size());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", result.get(0).getName());
    }

    @Test
    @DisplayName("Suscripción con notificación SMS")
    void subscribe_withSmsNotification() {
        testClient.setNotificationPreference(Client.NotificationType.SMS);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(fundRepository.findById(1L)).thenReturn(Optional.of(testFund));
        when(subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(1L, 1L)).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(notificationFactory.getService(Client.NotificationType.SMS)).thenReturn(notificationService);

        fundService.subscribe(1L, 1L);

        verify(notificationFactory).getService(Client.NotificationType.SMS);
        verify(notificationService).sendSubscriptionNotification(testClient, "FPV_BTG_PACTUAL_RECAUDADORA", 75000.0);
    }
}
