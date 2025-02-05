package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamRepositoryTest extends AbstractCoreRepositoryTest {


    @Test
    void findByKey_ok() {
        assertTrue(teamRepository.findAll().isEmpty());
        Team team = createTeam(createVenue());

        Team result = teamRepository.findByKey(team.getKey()).orElseThrow();
        assertEquals(team.getGender(), result.getGender());
        assertEquals(team.getKey(), result.getKey());
        assertEquals(team.getName(), result.getName());
        assertEquals(team.getContestType(), result.getContestType());
        assertEquals(team.getHomeVenue(), result.getHomeVenue());
    }

    @Test
    void findByVenue_ok() {
        assertTrue(fixtureRepository.findAll().isEmpty());
        Team team = createTeam(createVenue());

        Team result = teamRepository.findByHomeVenue(team.getHomeVenue()).get(0);
        assertEquals(team.getGender(), result.getGender());
        assertEquals(team.getKey(), result.getKey());
        assertEquals(team.getName(), result.getName());
        assertEquals(team.getContestType(), result.getContestType());
        assertEquals(team.getHomeVenue(), result.getHomeVenue());
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {
        Team team = createTeam(createVenue());
        team.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> teamRepository.saveAndFlush(team));
    }

    @Test
    void notNullGenderConstraint__exceptionThrown() {
        Team team = createTeam(createVenue());
        team.setGender(null);
        assertThrows(DataIntegrityViolationException.class, () -> teamRepository.saveAndFlush(team));
    }

    @Test
    void notNullContestTypeConstraint__exceptionThrown() {
        Team team = createTeam(createVenue());
        team.setContestType(null);
        assertThrows(DataIntegrityViolationException.class, () -> teamRepository.saveAndFlush(team));
    }

}
