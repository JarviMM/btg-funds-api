package com.btg.funds.dto;

import java.time.LocalDateTime;

public class TransactionResponse {

    private String transactionId;
    private String fundName;
    private String type;
    private Double amount;
    private LocalDateTime createdAt;

    public TransactionResponse() {}

    public TransactionResponse(String transactionId, String fundName, String type, Double amount, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.fundName = fundName;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getFundName() { return fundName; }
    public void setFundName(String fundName) { this.fundName = fundName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
