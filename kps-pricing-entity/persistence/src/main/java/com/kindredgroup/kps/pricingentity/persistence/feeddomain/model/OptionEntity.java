package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(schema = "pricing_entity", uniqueConstraints = {@UniqueConstraint(columnNames = {"option_id", "key"})})
public class OptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "option_id", nullable = false, updatable = false)
    private Option option;

    public OptionEntity() {
    }

    public OptionEntity(Option option, String key, String type) {
        this.option = option;
        this.key = key;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        this.key = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

}