package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(schema = "pricing_entity",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"proposition_id", "option_id", "variant_id"})})
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "proposition_id", nullable = false, updatable = false)
    private Proposition proposition;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "option_id")
    private Option option;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "variant_id")
    private Variant variant;

    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }


    protected Price() {

    }

    public Price(Option option, Variant variant, BigDecimal price) {
        if (!Objects.equals(option.getProposition().getId(), variant.getProposition().getId())) {
            throw new IllegalArgumentException("Failed create an outcome: variant and option propositions do not match");
        }
        setProposition(option.getProposition());
        setOption(option);
        setVariant(variant);
        this.price = price;
    }

    public Price(Option option, Variant variant) {
        this(option, variant, null);
    }

    public Long getId() {
        return id;
    }

    public Proposition getProposition() {
        return proposition;
    }

    private void setProposition(Proposition proposition) {
        Objects.requireNonNull(proposition);
        this.proposition = proposition;
    }

    public Option getOption() {
        return option;
    }

    private void setOption(Option option) {
        Objects.requireNonNull(option);
        this.option = option;
    }

    public Variant getVariant() {
        return variant;
    }

    private void setVariant(Variant variant) {
        Objects.requireNonNull(variant);
        this.variant = variant;
    }

    public Optional<BigDecimal> getPrice() {
        return Optional.ofNullable(price);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

}


