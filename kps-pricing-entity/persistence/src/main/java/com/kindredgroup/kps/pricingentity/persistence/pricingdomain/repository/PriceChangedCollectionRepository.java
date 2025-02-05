package com.kindredgroup.kps.pricingentity.persistence.pricingdomain.repository;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PriceChangedCollectionRepository extends Repository<Proposition, Long> {
    @Query(value = "select p.* from pricing_entity.proposition p where " +
            "p.contest_id in (select c.id from pricing_entity.contest c where c.key= ?1)",
            nativeQuery = true)
    List<Proposition> findByKey(String contestKey);
}
