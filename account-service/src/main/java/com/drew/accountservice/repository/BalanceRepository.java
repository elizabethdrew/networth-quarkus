package com.drew.accountservice.repository;

import com.drew.accountservice.entity.Account;
import com.drew.accountservice.entity.Balance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BalanceRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Balance save(Balance balance) {
        if (balance.getBalanceId() == null) {
            entityManager.persist(balance);
            return balance;
        }
        return entityManager.merge(balance);
    }

    public List<Balance> findAllByAccount(Account account) {
        return entityManager.createQuery(
                        "SELECT b FROM Balance b WHERE b.account = :account ORDER BY b.dateUpdated DESC",
                        Balance.class
                )
                .setParameter("account", account)
                .getResultList();
    }

    public Optional<Balance> findTopByAccountOrderByDateUpdatedDesc(Account account) {
        return entityManager.createQuery(
                        "SELECT b FROM Balance b WHERE b.account = :account ORDER BY b.dateUpdated DESC",
                        Balance.class
                )
                .setParameter("account", account)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<Balance> findByBalanceIdAndAccount(Long balanceId, Account account) {
        return entityManager.createQuery(
                        "SELECT b FROM Balance b WHERE b.balanceId = :balanceId AND b.account = :account",
                        Balance.class
                )
                .setParameter("balanceId", balanceId)
                .setParameter("account", account)
                .getResultStream()
                .findFirst();
    }

    public List<Balance> findLatestBalanceByAccountId(Long accountId) {
        return entityManager.createQuery(
                        "SELECT b FROM Balance b WHERE b.account.accountId = :accountId ORDER BY b.dateUpdated DESC",
                        Balance.class
                )
                .setParameter("accountId", accountId)
                .getResultList();
    }
}
