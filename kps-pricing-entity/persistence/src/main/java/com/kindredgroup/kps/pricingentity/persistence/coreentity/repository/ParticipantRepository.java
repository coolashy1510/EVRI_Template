package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByKey(String key);
}
