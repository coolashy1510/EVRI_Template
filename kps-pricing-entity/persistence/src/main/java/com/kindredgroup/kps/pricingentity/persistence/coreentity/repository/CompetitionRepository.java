package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    Optional<Competition> findByKey(String key);
}
