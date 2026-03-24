package com.btg.funds.dto;

import jakarta.validation.constraints.NotNull;

public class CancelSubscriptionRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clientId;

    @NotNull(message = "El ID del fondo es obligatorio")
    private Long fundId;

    public CancelSubscriptionRequest() {}

    public CancelSubscriptionRequest(Long clientId, Long fundId) {
        this.clientId = clientId;
        this.fundId = fundId;
    }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getFundId() { return fundId; }
    public void setFundId(Long fundId) { this.fundId = fundId; }
}
