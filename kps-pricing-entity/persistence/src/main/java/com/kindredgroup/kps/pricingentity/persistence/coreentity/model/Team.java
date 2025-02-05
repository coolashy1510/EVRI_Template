package com.kindredgroup.kps.pricingentity.persistence.coreentity.model;

import java.util.List;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter.CoreEntityGenderConverter;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import jakarta.persistence.*;

@Entity
@Table(schema = "core_entity")

public class Team {
    private static final String GENDER_CONSTRAINT = """
            varchar(20) CHECK (gender IN ('NotKnown', 'Male', 'Female', 'Mixed'))""";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text", updatable = false, unique = true, nullable = false)
    private String key;
    @Column(nullable = false)
    private String name;
    @Column(name = "contest_type", nullable = false)
    private String contestType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_venue_id")
    private Venue homeVenue;
    @Column(nullable = false, columnDefinition = GENDER_CONSTRAINT)
    @Convert(converter = CoreEntityGenderConverter.class)
    private CoreEntityGender gender;
    @ManyToMany (mappedBy = "teams")
    private List<Fixture> fixtures;
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    private List<TeamParticipantsMap> teamParticipantsMaps;

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

    public Venue getHomeVenue() {
        return homeVenue;
    }

    public void setHomeVenue(Venue homeVenue) {
        this.homeVenue = homeVenue;
    }

    public CoreEntityGender getGender() {
        return gender;
    }

    public void setGender(CoreEntityGender gender) {
        this.gender = gender;
    }

    public List<TeamParticipantsMap> getTeamParticipantsMaps() {
        return teamParticipantsMaps;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(List<Fixture> fixtures) {
        this.fixtures = fixtures;
    }

    public void setTeamParticipantsMaps(
            List<TeamParticipantsMap> teamParticipantsMaps) {
        this.teamParticipantsMaps = teamParticipantsMaps;
    }
}


