package com.kindredgroup.kps.pricingentity.persistence.coreentity.model;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter.CoreEntityGenderConverter;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(schema = "core_entity")

public class Participant {
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
    @Column(nullable = false, columnDefinition = GENDER_CONSTRAINT)
    @Convert(converter = CoreEntityGenderConverter.class)
    private CoreEntityGender gender;
    @Column(name = "date_of_birth", nullable = false)
    private OffsetDateTime dateOfBirth;

    @OneToMany(mappedBy = "participant", fetch = FetchType.EAGER)
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

    public CoreEntityGender getGender() {
        return gender;
    }

    public void setGender(CoreEntityGender gender) {
        this.gender = gender;
    }

    public OffsetDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(OffsetDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}


