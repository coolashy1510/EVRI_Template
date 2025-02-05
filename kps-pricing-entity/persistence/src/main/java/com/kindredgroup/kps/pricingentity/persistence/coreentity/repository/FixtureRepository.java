package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Fixture;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixtureRepository extends JpaRepository<Fixture, Long> {
    Optional<Fixture> findByKey(String key);

    List<Fixture> findByTournament(Tournament tournament);

}
