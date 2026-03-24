package com.btg.funds.service;

import com.btg.funds.dto.TransactionResponse;
import com.btg.funds.entity.*;
import com.btg.funds.exception.*;
import com.btg.funds.notification.NotificationFactory;
import com.btg.funds.notification.NotificationService;
import com.btg.funds.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FundService {

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationFactory notificationFactory;

    public FundService(FundRepository fundRepository,
                       ClientRepository clientRepository,
                       TransactionRepository transactionRepository,
                       SubscriptionRepository subscriptionRepository,
                       NotificationFactory notificationFactory) {
        this.fundRepository = fundRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationFactory = notificationFactory;
    }

    @Transactional
    public Transaction subscribe(Long clientId, Long fundId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clientId));

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new ResourceNotFoundException("Fondo no encontrado con ID: " + fundId));

        subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(clientId, fundId)
                .ifPresent(s -> { throw new DuplicateSubscriptionException(fund.getName()); });

        if (client.getBalance() < fund.getMinimumAmount()) {
            throw new InsufficientBalanceException(fund.getName());
        }

        client.setBalance(client.getBalance() - fund.getMinimumAmount());
        clientRepository.save(client);

        Subscription subscription = new Subscription(client, fund, fund.getMinimumAmount());
        subscriptionRepository.save(subscription);

        Transaction transaction = new Transaction(client, fund, Transaction.TransactionType.APERTURA, fund.getMinimumAmount());
        transactionRepository.save(transaction);

        NotificationService notifier = notificationFactory.getService(client.getNotificationPreference());
        notifier.sendSubscriptionNotification(client, fund.getName(), fund.getMinimumAmount());

        return transaction;
    }

    @Transactional
    public Transaction cancelSubscription(Long clientId, Long fundId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clientId));

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new ResourceNotFoundException("Fondo no encontrado con ID: " + fundId));

        Subscription subscription = subscriptionRepository.findByClientIdAndFundIdAndActiveTrue(clientId, fundId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró suscripción activa al fondo " + fund.getName()));

        client.setBalance(client.getBalance() + subscription.getAmount());
        clientRepository.save(client);

        subscription.setActive(false);
        subscriptionRepository.save(subscription);

        Transaction transaction = new Transaction(client, fund, Transaction.TransactionType.CANCELACION, subscription.getAmount());
        transactionRepository.save(transaction);

        return transaction;
    }

    public List<TransactionResponse> getTransactionHistory(Long clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clientId));

        return transactionRepository.findByClientIdOrderByCreatedAtDesc(clientId).stream()
                .map(t -> new TransactionResponse(
                        t.getTransactionId(),
                        t.getFund().getName(),
                        t.getType().name(),
                        t.getAmount(),
                        t.getCreatedAt()))
                .toList();
    }

    public List<Fund> getAllFunds() {
        return fundRepository.findAll();
    }

    public List<Subscription> getActiveSubscriptions(Long clientId) {
        return subscriptionRepository.findByClientIdAndActiveTrue(clientId);
    }

    public Client getClientInfo(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clientId));
    }
}
