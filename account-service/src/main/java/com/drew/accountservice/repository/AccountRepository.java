package com.drew.accountservice.repository;

import com.drew.accountservice.entity.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AccountRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Account save(Account account) {
        if (account.getAccountId() == null) {
            entityManager.persist(account);
            return account;
        }
        return entityManager.merge(account);
    }

    public List<Account> findAllByKeycloakUserId(String keycloakId) {
        return entityManager.createQuery(
                        "SELECT a FROM Account a WHERE a.keycloakUserId = :keycloakUserId",
                        Account.class
                )
                .setParameter("keycloakUserId", keycloakId)
                .getResultList();
    }

    public Optional<Account> findByAccountIdAndKeycloakUserId(Long accountId, String keycloakUserId) {
        return entityManager.createQuery(
                        "SELECT a FROM Account a WHERE a.accountId = :accountId AND a.keycloakUserId = :keycloakUserId",
                        Account.class
                )
                .setParameter("accountId", accountId)
                .setParameter("keycloakUserId", keycloakUserId)
                .getResultStream()
                .findFirst();
    }

    public Optional<Account> findByTruelayerAccountIdAndKeycloakUserId(String truelayerAccountId, String keycloakUserId) {
        return entityManager.createQuery(
                        "SELECT a FROM Account a WHERE a.truelayerAccountId = :truelayerAccountId AND a.keycloakUserId = :keycloakUserId",
                        Account.class
                )
                .setParameter("truelayerAccountId", truelayerAccountId)
                .setParameter("keycloakUserId", keycloakUserId)
                .getResultStream()
                .findFirst();
    }
}
