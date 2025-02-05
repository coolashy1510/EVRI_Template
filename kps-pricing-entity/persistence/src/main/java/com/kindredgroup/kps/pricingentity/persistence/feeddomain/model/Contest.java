package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter.ContestStatusConverter;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter.ContestTypeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(schema = "pricing_entity") // TODO:nikita.shvinagir:2023-01-26: update(?) the schema mapping when it is set up remotely
public class Contest {

    private static final String STATUS_CONSTRAINT = """
            varchar(20) CHECK (status IN ('Cancelled', 'Concluded', 'InPlay', 'PreGame'))""";

    private static final String TYPE_CONSTRAINT = """
            varchar(50) CHECK (type IN ('AlpineSkiing', 'Athletics', 'AussieRules', 'Badminton', 'Bandy', 'Baseball',
            'Basketball', 'Basketball3x3', 'BeachSoccer', 'BeachVolleyball', 'Biathlon', 'Bobsleigh', 'Bowls', 'Boxing', 'Chess',
            'Cricket', 'CrossCountry', 'Curling', 'Cycling', 'Darts', 'EBasketball', 'EIceHockey', 'ESoccer',
            'ESportArenaOfValor', 'ESportCallOfDuty', 'ESportCounterStrike', 'ESportDota','ESportKingOfGlory',
            'ESportLeagueOfLegends', 'ESportOverwatch', 'ESportRainbowSix', 'ESportRocketLeague', 'ESportStarCraft',
            'ESportValorant', 'ESportWildRift', 'FieldHockey', 'FigureSkating', 'Floorball', 'Football', 'Formula1', 'FormulaE',
            'FreestyleSkiing', 'Futsal', 'GaelicFootball', 'GaelicHurling', 'Golf', 'Greyhounds', 'Handball', 'Harness',
            'Hearthstone', 'IceHockey', 'Kabaddi', 'Lacrosse', 'Luge', 'Mma', 'MotorcycleRacing', 'Netball', 'NordicCombined',
            'Olympics', 'Padel', 'Pesapallo', 'Pool', 'RinkHockey', 'Rugby', 'Sailing', 'ShortTrack', 'Skeleton', 'SkiJumping',
            'Snooker', 'Snowboard', 'SpeedSkating', 'Speedway', 'Squash', 'StockCarRacing', 'Surfing', 'Swimming', 'TableTennis',
            'Tennis', 'Thoroughbred', 'VirtualGreyhounds', 'VirtualHarness','VirtualThoroughbred', 'Volleyball', 'Waterpolo'))""";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", updatable = false, unique = true, nullable = false)
    private String key;
    @Column(nullable = false)
    private String name;
    @Column(name = "start_date_time")
    private OffsetDateTime startDateTime;
    @Column(nullable = false, columnDefinition = STATUS_CONSTRAINT)
    @Convert(converter = ContestStatusConverter.class)
    private ContestStatus status;
    @Column(nullable = false, columnDefinition = TYPE_CONSTRAINT)
    @Convert(converter = ContestTypeConverter.class)
    private ContestType type;

    @OneToMany(mappedBy = "contest", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proposition> propositions = new ArrayList<>();

    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

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

    public ContestStatus getStatus() {
        return status;
    }

    public void setStatus(ContestStatus status) {
        this.status = status;
    }

    public ContestType getType() {
        return type;
    }

    public void setType(ContestType type) {
        this.type = type;
    }
    public void setStartDateTime(OffsetDateTime startDateTime) { this.startDateTime = startDateTime; }

    public List<Proposition> getPropositions() {
        return propositions;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

}


