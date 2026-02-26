package com.drew.truelayerservice.repository;

import com.drew.truelayerservice.entity.Token;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class TokenRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Token save(Token token) {
        if (token.getId() == null) {
            entityManager.persist(token);
            return token;
        }
        return entityManager.merge(token);
    }

    public Optional<Token> findByUserId(String userId) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.userId = :userId",
                        Token.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }
}
