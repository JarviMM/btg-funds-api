package com.btg.funds.notification;

import com.btg.funds.entity.Client;

public interface NotificationService {

    void sendSubscriptionNotification(Client client, String fundName, Double amount);
}
