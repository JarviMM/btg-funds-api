package com.btg.funds.controller;

import com.btg.funds.dto.*;
import com.btg.funds.entity.Fund;
import com.btg.funds.entity.Subscription;
import com.btg.funds.entity.Transaction;
import com.btg.funds.entity.Client;
import com.btg.funds.service.FundService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class FundController {

    private final FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<ApiResponse<TransactionResponse>> subscribe(
            @Valid @RequestBody SubscriptionRequest request) {
        Transaction transaction = fundService.subscribe(request.getClientId(), request.getFundId());
        TransactionResponse response = new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getFund().getName(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Suscripción exitosa al fondo " + transaction.getFund().getName(), response));
    }

    @DeleteMapping("/subscriptions")
    public ResponseEntity<ApiResponse<TransactionResponse>> cancelSubscription(
            @Valid @RequestBody CancelSubscriptionRequest request) {
        Transaction transaction = fundService.cancelSubscription(request.getClientId(), request.getFundId());
        TransactionResponse response = new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getFund().getName(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.ok("Cancelación exitosa del fondo " + transaction.getFund().getName(), response));
    }

    @GetMapping("/transactions/{clientId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(@PathVariable Long clientId) {
        List<TransactionResponse> transactions = fundService.getTransactionHistory(clientId);
        return ResponseEntity.ok(ApiResponse.ok("Historial de transacciones", transactions));
    }

    @GetMapping("/funds")
    public ResponseEntity<ApiResponse<List<Fund>>> getAllFunds() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de fondos disponibles", fundService.getAllFunds()));
    }

    @GetMapping("/subscriptions/{clientId}")
    public ResponseEntity<ApiResponse<List<Subscription>>> getActiveSubscriptions(@PathVariable Long clientId) {
        return ResponseEntity.ok(ApiResponse.ok("Suscripciones activas", fundService.getActiveSubscriptions(clientId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/clients/{clientId}")
    public ResponseEntity<ApiResponse<Client>> getClientInfo(@PathVariable Long clientId) {
        return ResponseEntity.ok(ApiResponse.ok("Información del cliente", fundService.getClientInfo(clientId)));
    }
}
