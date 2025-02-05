package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByKey(String key);
}
