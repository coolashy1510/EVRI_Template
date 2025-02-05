package com.kindredgroup.kps.pricingentity.persistence.coreentity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class TeamParticipantsMapKey implements Serializable {
    @Column(name = "team_id")
    private Long teamId;
    @Column(name = "participant_id")
    private Long participantId;
    @Column(name = "tournament_id")
    private Long tournamentId;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public Long getParticipantId() { return participantId; }

    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public Long getTournamentId() { return tournamentId; }

    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }
}


