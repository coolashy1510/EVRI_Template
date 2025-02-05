package com.kindredgroup.kps.pricingentity.persistence.coreentity.model;

import java.util.List;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter.CompetitionAgeCategoryConverter;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter.CompetitionTypeConverter;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter.CoreEntityGenderConverter;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(schema = "core_entity")
public class Competition {

    private static final String TYPE_CONSTRAINT = """
            varchar(20) CHECK (type IN ('NotApplicable', 'Cup', 'League'))""";
    private static final String AGE_CATEGORY_CONSTRAINT = """
            varchar(20) CHECK (age_category IN ('NotApplicable', 'Unconfirmed', 'Y10', 'U11', 'U12', 'U13', 'U14', 'U15',
            'U16', 'U17', 'U18', 'U19', 'U20', 'U21', 'U23', 'U22', 'Juniors', 'Senior', 'Youth'))""";
    private static final String GENDER_CONSTRAINT = """
            varchar(20) CHECK (gender IN ('NotKnown', 'Male', 'Female', 'Mixed'))""";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text", updatable = false, unique = true, nullable = false)
    private String key;
    @Column(nullable = false, columnDefinition = TYPE_CONSTRAINT)
    @Convert(converter = CompetitionTypeConverter.class)
    private CompetitionType type;
    @Column(nullable = false)
    private String name;
    @Column(name = "contest_type", nullable = false)
    private String contestType;
    @Column(name = "age_category", nullable = false, columnDefinition = AGE_CATEGORY_CONSTRAINT)
    @Convert(converter = CompetitionAgeCategoryConverter.class)
    private CompetitionAgeCategory ageCategory;
    @Column(nullable = false, columnDefinition = GENDER_CONSTRAINT)
    @Convert(converter = CoreEntityGenderConverter.class)
    private CoreEntityGender gender;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY)
    private List<Tournament> tournaments;

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CompetitionType getType() {
        return type;
    }

    public void setType(CompetitionType type) {
        this.type = type;
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

    public CompetitionAgeCategory getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(CompetitionAgeCategory ageCategory) {
        this.ageCategory = ageCategory;
    }

    public CoreEntityGender getGender() {
        return gender;
    }

    public void setGender(CoreEntityGender gender) {
        this.gender = gender;
    }
}


