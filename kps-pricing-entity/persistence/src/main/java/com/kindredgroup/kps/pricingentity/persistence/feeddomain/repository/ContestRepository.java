package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    Optional<Contest> findByKey(String key);

    @Query("select c.type from Contest c where c.key = ?1")
    Optional<ContestType> getContestTypeByKey(String key);

    @Query(value = "select id from pricing_entity.contest c where" +
            "(c.status in ('Concluded','Cancelled','Suspended') and c.updated_at < now() - interval '7' day) limit ?1",
           nativeQuery = true)
    List<Long> getExpired(long limit);

    @Query(value = "select id from pricing_entity.contest c where" +
            " c.status not in ('Concluded','Cancelled','Suspended') and c.start_date_time  < now() - interval '14' day",
            nativeQuery = true)
    List<Long> getCorrupted();
}
