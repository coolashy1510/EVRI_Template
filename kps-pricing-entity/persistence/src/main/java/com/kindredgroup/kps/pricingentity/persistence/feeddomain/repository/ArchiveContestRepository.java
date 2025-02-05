package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArchiveContestRepository extends JpaRepository<Contest, Long> {

    @Query(value = "select id from pricing_entity.archived_contest c where" +
            "(c.status in ('Concluded','Cancelled','Suspended') and c.updated_at < now() - interval '14' day) limit ?1",
           nativeQuery = true)
    List<Long> getExpired(long limit);

}
