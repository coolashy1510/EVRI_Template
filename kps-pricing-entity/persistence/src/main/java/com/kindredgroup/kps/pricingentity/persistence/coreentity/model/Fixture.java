package com.kindredgroup.kps.pricingentity.persistence.coreentity.model;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter.FixtureRoundTypeConverter;
import com.kindredgroup.kps.pricingentity.coreentity.domain.FixtureRoundType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(schema = "core_entity")

public class Fixture {

    private static final String ROUND_TYPE_CONSTRAINT = """
            varchar(50) CHECK (round_type IN ('Unknown', 'Preliminary', 'PreliminaryFirstRound', 'PreliminarySecondRound',
            'PreliminaryThirdRound', 'PreliminarySemi', 'PreliminaryFinal', 'Group',
            'KnockOut', 'RoundOf128', 'RoundOf64', 'RoundOf32', 'RoundOf16', 'Quarter',
            'Semi', 'Final'))""";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text", updatable = false, unique = true, nullable = false)
    private String key;
    @Column(nullable = false)
    private String name;
    @Column(name = "contest_type", nullable = false)
    private String contestType;
    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "round_type", columnDefinition = ROUND_TYPE_CONSTRAINT)
    @Convert(converter = FixtureRoundTypeConverter.class)
    private FixtureRoundType roundType;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;
    @ManyToMany
    @JoinTable(
            schema = "core_entity",
            name = "team_fixture",
            joinColumns = @JoinColumn(name = "fixture_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> teams;

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContestType() {
        return contestType;
    }

    public void setContestType(String contestType) {
        this.contestType = contestType;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public FixtureRoundType getRoundType() {
        return roundType;
    }

    public void setRoundType(FixtureRoundType roundType) {
        this.roundType = roundType;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}


