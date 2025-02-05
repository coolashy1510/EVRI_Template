package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.TeamParticipantsMap;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.TeamParticipantsMapKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamParticipantMapRepository extends JpaRepository<TeamParticipantsMap, TeamParticipantsMapKey> {
}
