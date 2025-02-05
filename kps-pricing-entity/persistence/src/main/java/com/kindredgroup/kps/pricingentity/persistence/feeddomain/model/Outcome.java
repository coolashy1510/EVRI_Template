package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(schema = "pricing_entity",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"proposition_id", "option_id", "variant_id"})})
public class Outcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "proposition_id", nullable = false)
    private Long propositionId;
    @Column(name = "option_id", nullable = false)
    private Long optionId;
    @Column(name = "variant_id", nullable = false)
    private Long variantId;
    @Column(name = "refund_numerator")
    private Integer refundNumerator;

    @Column(name = "refund_denominator")
    private Integer refundDenominator;

    @Column(name = "win_numerator")
    private Integer winNumerator;

    @Column(name = "win_denominator")
    private Integer winDenominator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposition_id", insertable = false, updatable = false)
    private Proposition proposition;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", insertable = false, updatable = false)
    private Option option;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", insertable = false, updatable = false)
    private Variant variant;

    protected Outcome() {

    }

    public Outcome(Option option, Variant variant, Integer refundNumerator, Integer refundDenominator, Integer winNumerator, Integer winDenominator) {
        if (!Objects.equals(option.getProposition().getId(), variant.getProposition().getId())) {
            throw new IllegalArgumentException("Failed create an outcome: variant and option propositions do not match");
        }
        setProposition(option.getProposition());
        setOption(option);
        setVariant(variant);
        setRefundNumerator(refundNumerator);
        setRefundDenominator(refundDenominator);
        setWinNumerator(winNumerator);
        setWinDenominator(winDenominator);
    }

    public Long getId() {
        return id;
    }

    public Long getPropositionId() {
        return propositionId;
    }

    public Proposition getProposition() {
        return proposition;
    }

    private void setProposition(Proposition proposition) {
        Objects.requireNonNull(proposition);
        this.propositionId = proposition.getId();
        this.proposition = proposition;
    }

    public Option getOption() {
        return option;
    }

    private void setOption(Option option) {
        Objects.requireNonNull(option);
        this.option = option;
        this.optionId = option.getId();
    }

    public Variant getVariant() {
        return variant;
    }

    private void setVariant(Variant variant) {
        Objects.requireNonNull(variant);
        this.variant = variant;
        this.variantId = variant.getId();
    }

    public Long getOptionId() {
        return optionId;
    }

    public Long getVariantId() {
        return variantId;
    }

    public Integer getRefundNumerator() {
        return refundNumerator;
    }

    public void setRefundNumerator(Integer refundNumerator) {
        this.refundNumerator = refundNumerator;
    }

    public Integer getRefundDenominator() {
        return refundDenominator;
    }

    public void setRefundDenominator(Integer refundDenominator) {
        this.refundDenominator = refundDenominator;
    }

    public Integer getWinNumerator() {
        return winNumerator;
    }

    public void setWinNumerator(Integer winNumerator) {
        this.winNumerator = winNumerator;
    }

    public Integer getWinDenominator() {
        return winDenominator;
    }

    public void setWinDenominator(Integer winDenominator) {
        this.winDenominator = winDenominator;
    }
}


