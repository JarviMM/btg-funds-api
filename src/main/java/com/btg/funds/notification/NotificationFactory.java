package com.btg.funds.notification;

import com.btg.funds.entity.Client;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    private final NotificationService emailService;
    private final NotificationService smsService;

    public NotificationFactory(
            @Qualifier("emailNotificationService") NotificationService emailService,
            @Qualifier("smsNotificationService") NotificationService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    public NotificationService getService(Client.NotificationType type) {
        return switch (type) {
            case EMAIL -> emailService;
            case SMS -> smsService;
        };
    }
}
