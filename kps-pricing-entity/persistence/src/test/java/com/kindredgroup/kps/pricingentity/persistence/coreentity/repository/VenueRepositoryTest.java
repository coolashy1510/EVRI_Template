package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VenueRepositoryTest extends AbstractCoreRepositoryTest {


    @Test
    void findByKey_ok() {
        assertTrue(venueRepository.findAll().isEmpty());
        Venue venue = createVenue();
        Venue result = venueRepository.findByKey(venue.getKey()).orElseThrow();
        assertEquals(venue.getKey(), result.getKey());
        assertEquals(venue.getName(), result.getName());
        assertEquals(venue.getContestType(), result.getContestType());
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {
        Venue venue = createVenue();
        venue.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> venueRepository.saveAndFlush(venue));
    }

    @Test
    void notNullContestTypeConstraint__exceptionThrown() {
        Venue venue = createVenue();
        venue.setContestType(null);
        assertThrows(DataIntegrityViolationException.class, () -> venueRepository.saveAndFlush(venue));
    }

}
