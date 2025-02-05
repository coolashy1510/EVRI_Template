package com.kindredgroup.kps.pricingentity.persistence.coreentity.model;

import jakarta.persistence.*;

@Entity(name = "team_participant")
@Table(schema = "core_entity")
@IdClass(TeamParticipantsMapKey.class)
public class TeamParticipantsMap {

    @Id
    private Long participantId;
    @Id
    private Long tournamentId;
    @Id
    private Long teamId;

    @ManyToOne()
    @MapsId()
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne()
    @MapsId()
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne()
    @MapsId()
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;


    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

}


