package com.networth.userservice.config;

import com.networth.userservice.dto.TaxRate;
import com.networth.userservice.entity.User;
import com.networth.userservice.repository.UserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationScoped
public class UserInitializer {

    private final UserRepository userRepository;
    private final boolean seedUserEnabled;

    public UserInitializer(
            UserRepository userRepository,
            @ConfigProperty(name = "user.seed.enabled", defaultValue = "true") boolean seedUserEnabled
    ) {
        this.userRepository = userRepository;
        this.seedUserEnabled = seedUserEnabled;
    }

    void insertSeedUser(@Observes StartupEvent event) {
        if (!seedUserEnabled) {
            return;
        }

        insertSeedUserTransactional();
    }

    @Transactional
    void insertSeedUserTransactional() {
        
        if (userRepository.existsByUsername("seeduser")) {
            return;
        }

        User user = new User();
        user.setKeycloakId("0c77e0c4-95f4-4704-a24f-ee22deb43609");
        user.setUsername("seeduser");
        user.setEmail("seeduser@example.co.uk");
        user.setActiveUser(true);
        user.setTaxRate(TaxRate.BASIC);
        user.setDateOfBirth(LocalDate.parse("1990-01-01"));
        user.setDateOpened(LocalDateTime.parse("2023-06-06T12:00:00"));
        user.setDateUpdated(LocalDateTime.parse("2023-10-06T12:00:00"));

        userRepository.save(user);
    }
}
