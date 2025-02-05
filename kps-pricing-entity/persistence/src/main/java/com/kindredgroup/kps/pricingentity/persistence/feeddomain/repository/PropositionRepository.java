package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropositionRepository extends JpaRepository<Proposition, Long> {

    Optional<Proposition> findByContestAndKey(Contest contest, String key);

    //TODO:nikita.shvinagir:2023-02-23; check if JPA allows renaming to "findByContest"
    List<Proposition> findPropositionsByContest(Contest contest);

    Optional<Proposition> findByContestKeyAndKey(String contestKey, String propositionKey);
}
