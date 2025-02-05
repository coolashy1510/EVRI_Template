package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Outcome;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface OutcomeRepository extends JpaRepository<Outcome, Long> {
    List<Outcome> findByProposition(Proposition proposition);

    @Query("select o from Outcome o where o.proposition.key = ?1 and o.option.key = ?2 and o.variant.key = ?3")
    Optional<Outcome> findByKey(String propositionKey, String optionKey, String variantKey);

    @Query("select o from Outcome o where o.propositionId in (select p.id from Proposition p where p.contest.key = ?1)")
    List<Outcome> findByContestKey(String contestKey);
}
