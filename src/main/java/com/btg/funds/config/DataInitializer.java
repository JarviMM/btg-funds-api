package com.btg.funds.config;

import com.btg.funds.entity.Client;
import com.btg.funds.entity.Fund;
import com.btg.funds.entity.User;
import com.btg.funds.repository.ClientRepository;
import com.btg.funds.repository.FundRepository;
import com.btg.funds.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(FundRepository fundRepository, ClientRepository clientRepository,
                           UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.fundRepository = fundRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (fundRepository.count() == 0) {
            log.info("Inicializando fondos de inversión...");
            fundRepository.save(new Fund("FPV_BTG_PACTUAL_RECAUDADORA", 75000.0, "FPV"));
            fundRepository.save(new Fund("FPV_BTG_PACTUAL_ECOPETROL", 125000.0, "FPV"));
            fundRepository.save(new Fund("DEUDAPRIVADA", 50000.0, "FIC"));
            fundRepository.save(new Fund("FDO-ACCIONES", 250000.0, "FIC"));
            fundRepository.save(new Fund("FPV_BTG_PACTUAL_DINAMICA", 100000.0, "FPV"));
            log.info("Fondos inicializados correctamente.");
        }

        if (clientRepository.count() == 0) {
            log.info("Creando cliente de prueba...");
            clientRepository.save(new Client(
                    "Juan Pérez",
                    "juan.perez@email.com",
                    "+573001234567",
                    500000.0,
                    Client.NotificationType.EMAIL
            ));
            log.info("Cliente de prueba creado con saldo inicial de COP $500.000");
        }

        if (userRepository.count() == 0) {
            log.info("Creando usuarios de prueba...");
            userRepository.save(new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@btg.com",
                    User.Role.ADMIN
            ));
            userRepository.save(new User(
                    "user",
                    passwordEncoder.encode("user123"),
                    "user@btg.com",
                    User.Role.USER
            ));
            log.info("Usuarios creados: admin/admin123 (ADMIN), user/user123 (USER)");
        }
    }
}
