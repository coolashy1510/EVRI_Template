package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Team;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByKey(String key);

    List<Team> findByHomeVenue(Venue venue);
}
