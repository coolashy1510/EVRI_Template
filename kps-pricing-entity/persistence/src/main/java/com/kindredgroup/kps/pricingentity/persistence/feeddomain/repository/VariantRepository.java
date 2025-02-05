package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<Variant, Long> {
    Optional<Variant> findByPropositionAndKey(Proposition proposition, String key);

    List<Variant> findByProposition(Proposition proposition);

}
