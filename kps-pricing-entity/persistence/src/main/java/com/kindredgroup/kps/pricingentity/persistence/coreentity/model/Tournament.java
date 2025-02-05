package com.kindredgroup.kps.pricingentity.persistence.coreentity.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(schema = "core_entity")

public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text", updatable = false, unique = true, nullable = false)
    private String key;
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "competition_id")
    private Competition competition;
    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;
    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Column(nullable = false)
    @OneToMany(mappedBy = "tournament", fetch = FetchType.LAZY)
    private List<Fixture> fixtures;

    @OneToMany(mappedBy = "tournament", fetch = FetchType.EAGER)
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

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(OffsetDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}


