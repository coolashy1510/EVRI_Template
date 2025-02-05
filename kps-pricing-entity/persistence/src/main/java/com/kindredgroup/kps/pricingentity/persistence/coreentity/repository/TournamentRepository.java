package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Optional<Tournament> findByKey(String key);

    List<Tournament> findTournamentByCompetition(Competition competition);
}
