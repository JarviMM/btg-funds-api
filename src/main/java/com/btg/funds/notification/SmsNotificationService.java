package com.btg.funds.notification;

import com.btg.funds.entity.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("smsNotificationService")
public class SmsNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    @Override
    public void sendSubscriptionNotification(Client client, String fundName, Double amount) {
        log.info("Enviando SMS a {} ({}): Suscripción exitosa al fondo {} por COP ${}",
                client.getName(), client.getPhone(), fundName, String.format("%,.0f", amount));
    }
}
