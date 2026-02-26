package com.drew.isaservice.repository;

import com.drew.commonlibrary.types.TaxYear;
import com.drew.isaservice.entity.Isa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class IsaRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Isa save(Isa isa) {
        if (isa.getId() == null) {
            entityManager.persist(isa);
            return isa;
        }
        return entityManager.merge(isa);
    }

    public Optional<Isa> findByKeycloakIdAndTaxYear(String keycloakId, TaxYear taxYear) {
        return entityManager.createQuery(
                        "SELECT i FROM Isa i WHERE i.keycloakId = :keycloakId AND i.taxYear = :taxYear",
                        Isa.class
                )
                .setParameter("keycloakId", keycloakId)
                .setParameter("taxYear", taxYear)
                .getResultStream()
                .findFirst();
    }
}
