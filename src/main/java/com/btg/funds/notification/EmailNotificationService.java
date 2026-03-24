package com.btg.funds.notification;

import com.btg.funds.entity.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("emailNotificationService")
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public void sendSubscriptionNotification(Client client, String fundName, Double amount) {
        log.info("Enviando EMAIL a {} ({}): Suscripción exitosa al fondo {} por COP ${}",
                client.getName(), client.getEmail(), fundName, String.format("%,.0f", amount));
    }
}
