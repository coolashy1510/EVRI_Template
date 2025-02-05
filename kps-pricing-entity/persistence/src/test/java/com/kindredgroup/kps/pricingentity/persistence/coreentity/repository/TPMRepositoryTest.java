package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Participant;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Team;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.TeamParticipantsMap;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.TeamParticipantsMapKey;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TPMRepositoryTest extends AbstractCoreRepositoryTest {

    @Autowired
    protected TeamParticipantMapRepository teamParticipantMapRepository;


    @Test
    void tpm_ok() {
        assertTrue(teamParticipantMapRepository.findAll().isEmpty());

        Competition competition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17, CoreEntityGender.FEMALE);

        Tournament tournament = createTournament(competition);
        Participant participant = createParticipant();
        Team team = createTeam(createVenue());
        TeamParticipantsMap teamParticipantsMap = new TeamParticipantsMap();
        teamParticipantsMap.setTeam(team);
        teamParticipantsMap.setTournament(tournament);
        teamParticipantsMap.setParticipant(participant);
        teamParticipantsMap.setTeamId(team.getId());
        teamParticipantsMap.setTournamentId(tournament.getId());
        teamParticipantsMap.setParticipantId(participant.getId());
        teamParticipantMapRepository.save(teamParticipantsMap);

        TeamParticipantsMapKey teamParticipantsMapKey = new TeamParticipantsMapKey();
        teamParticipantsMapKey.setTeamId(team.getId());
        teamParticipantsMapKey.setParticipantId(participant.getId());
        teamParticipantsMapKey.setTournamentId(tournament.getId());

        TeamParticipantsMap teamParticipantsMapSaved = teamParticipantMapRepository.findById(teamParticipantsMapKey).orElseThrow();
        assertEquals(teamParticipantsMapSaved.getParticipant(), participant);
        assertEquals(teamParticipantsMapSaved.getTeam(), team);
        assertEquals(teamParticipantsMapSaved.getTournament(), tournament);
    }

    @Test
    void tpm_partialNullKey() {
        assertTrue(teamParticipantMapRepository.findAll().isEmpty());

        Competition competition = createCompetition(CompetitionType.LEAGUE, CompetitionAgeCategory.U17, CoreEntityGender.FEMALE);

        Tournament tournament = createTournament(competition);
        Participant participant = createParticipant();
        Team team = createTeam(createVenue());
        TeamParticipantsMap teamParticipantsMap = new TeamParticipantsMap();
        teamParticipantsMap.setTeam(team);
        teamParticipantsMap.setTournament(tournament);
        teamParticipantsMap.setParticipant(participant);
        teamParticipantsMap.setTeamId(team.getId());
        teamParticipantsMap.setTournamentId(tournament.getId());
        teamParticipantsMap.setParticipantId(participant.getId());
        teamParticipantMapRepository.save(teamParticipantsMap);

        TeamParticipantsMapKey teamParticipantsMapKey = new TeamParticipantsMapKey();
        teamParticipantsMapKey.setTeamId(team.getId());
        teamParticipantsMapKey.setParticipantId(participant.getId());

        assertFalse(teamParticipantMapRepository.findById(teamParticipantsMapKey).isPresent());
    }


}
