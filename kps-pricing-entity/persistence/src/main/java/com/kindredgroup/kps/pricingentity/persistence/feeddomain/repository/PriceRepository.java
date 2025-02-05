package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.util.List;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Price;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByProposition(Proposition proposition);

}
