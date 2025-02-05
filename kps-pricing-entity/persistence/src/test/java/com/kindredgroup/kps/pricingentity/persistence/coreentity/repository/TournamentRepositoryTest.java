package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TournamentRepositoryTest extends AbstractCoreRepositoryTest {

    @Test
    void tournament_ok() {
        assertTrue(competitionRepository.findAll().isEmpty());

        Competition competition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17, CoreEntityGender.FEMALE);

        Tournament tournament = createTournament(competition);
        final String tournamentKey = tournament.getKey();


        Tournament result = tournamentRepository.findByKey(tournamentKey).orElseThrow();
        assertEquals(tournament.getName(), result.getName());
        assertEquals(tournament.getStartsAt(), result.getStartsAt());
        assertEquals(tournament.getEndsAt(), result.getEndsAt());
        assertNotNull(tournament.getCompetition());
        assertEquals(tournament.getKey(), result.getKey());

        tournament = createTournament(competition);
        final String tournamentKey2 = tournament.getKey();

        List<Tournament> items = tournamentRepository.findTournamentByCompetition(competition);
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getKey().equals(tournamentKey)));
        assertTrue(items.stream().anyMatch(item -> item.getKey().equals(tournamentKey2)));

    }

    @Test
    void keyConstraint_exceptionThrown() {
        Competition competition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17, CoreEntityGender.FEMALE);

        Tournament existingTournament = createTournament(competition);

        Tournament tournament = new Tournament();
        tournament.setKey(existingTournament.getKey());
        tournament.setName("WorldCup 2022");
        tournament.setCompetition(competition);

        assertThrows(DataIntegrityViolationException.class, () -> tournamentRepository.save(tournament));

        Competition anotherCompetition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17,
                CoreEntityGender.FEMALE);

        Tournament tournament1 = new Tournament();
        tournament1.setCompetition(anotherCompetition);
        tournament1.setKey(existingTournament.toString());
        tournament1.setName("WorldCup 2022");
        tournament1.setStartsAt(OffsetDateTime.now());
        tournament1.setEndsAt(OffsetDateTime.now().plus(7L, ChronoUnit.DAYS));
        tournamentRepository.save(tournament1);
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {
        Competition competition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17, CoreEntityGender.FEMALE);

        final Tournament tournament = createTournament(competition);
        tournament.setName(null);
        tournament.setStartsAt(OffsetDateTime.now());
        tournament.setEndsAt(OffsetDateTime.now().plus(7L, ChronoUnit.DAYS));
        assertThrows(DataIntegrityViolationException.class, () -> tournamentRepository.saveAndFlush(tournament));
    }

    @Test
    void notNullStartsAtConstraint__exceptionThrown() {
        Competition competition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17, CoreEntityGender.FEMALE);

        final Tournament tournament = createTournament(competition);
        tournament.setName("WorldCup 2022");
        tournament.setStartsAt(null);
        tournament.setEndsAt(OffsetDateTime.now().plus(7L, ChronoUnit.DAYS));
        assertThrows(DataIntegrityViolationException.class, () -> tournamentRepository.saveAndFlush(tournament));
    }

    @Test
    void notNullEndsAtConstraint__exceptionThrown() {
        Competition competition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17, CoreEntityGender.FEMALE);

        final Tournament tournament = createTournament(competition);
        tournament.setName("WorldCup 2022");
        tournament.setStartsAt(OffsetDateTime.now());
        tournament.setEndsAt(null);
        assertThrows(DataIntegrityViolationException.class, () -> tournamentRepository.saveAndFlush(tournament));
    }


}
