package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter.OptionTypeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(schema = "pricing_entity", uniqueConstraints = {@UniqueConstraint(columnNames = {"proposition_id", "key"})})
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "proposition_id", nullable = false, updatable = false)
    private Proposition proposition;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Convert(converter = OptionTypeConverter.class)
    private OptionType type;

    @OneToMany(mappedBy = "option", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionEntity> optionEntities = new ArrayList<>();

    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    protected Option() {
    }

    public Option(String key, Proposition proposition, OptionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Option type should not be null");
        }
        this.key = key;
        setProposition(proposition);
        this.type = type;
    }

    public OptionType getType() {
        return type;
    }

    public Proposition getProposition() {
        return proposition;
    }

    private void setProposition(Proposition proposition) {
        if (proposition == null) {
            throw new IllegalArgumentException("Option proposition should not be null");
        }
        this.proposition = proposition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public List<OptionEntity> getOptionEntities() {
        return optionEntities;
    }

    public void setOptionEntities(List<OptionEntity> optionEntities) {
        this.optionEntities = optionEntities;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

}


