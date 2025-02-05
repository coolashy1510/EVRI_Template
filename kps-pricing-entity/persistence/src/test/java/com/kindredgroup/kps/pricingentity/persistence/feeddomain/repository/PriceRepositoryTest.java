package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Price;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    PriceRepository priceRepository;

    @Autowired
    VariantRepository variantRepository;
    @Autowired
    OptionRepository optionRepository;
    @Autowired
    PropositionRepository propositionRepository;
    private Contest contest;
    private Proposition proposition;
    private Variant lineVariant;
    private Variant marginVariant;
    private Option option;

    @BeforeEach
    void setUp() {
        contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        proposition = new Proposition();
        proposition.setKey("total_incl_overtime");
        proposition.setContest(contest);
        proposition.setName("Football simple");
        proposition.setType("total");

        String variantKey = "line";
        lineVariant = new Variant(variantKey, proposition, VariantType.LINE);
        lineVariant.setName("Line");

        variantKey = "margin";
        marginVariant = new Variant(variantKey, proposition, VariantType.MARGIN);
        marginVariant.setName("Margin");

        final String optionKey = "key1";
        option = new Option(optionKey, proposition, OptionType.DRAW);
        option.setName("Name");

        propositionRepository.saveAndFlush(proposition);
    }

    @Test
    void price_ok() {
        assertTrue(priceRepository.findAll().isEmpty());

        Price emptyPrice = new Price(option, lineVariant);
        priceRepository.save(emptyPrice);

        Price price = new Price(option, marginVariant, BigDecimal.TEN);
        priceRepository.save(price);

        assertEquals(2, priceRepository.findAll().size());

        assertTrue(priceRepository.findByProposition(proposition).stream()
                                  .filter(item -> item.getVariant().getId().equals(lineVariant.getId())).findFirst().orElseThrow()
                                  .getPrice().isEmpty());
        assertEquals(0, BigDecimal.TEN.compareTo(priceRepository.findByProposition(proposition).stream()
                                                                .filter(item -> item.getVariant().getId()
                                                                                    .equals(marginVariant.getId()))
                                                                .findFirst().orElseThrow()
                                                                .getPrice().orElseThrow()));
//        test replace trigger with JPA methods
        OffsetDateTime savedTimestamp = emptyPrice.getUpdatedAt();
        assertNotNull(savedTimestamp);
        assertTrue(savedTimestamp.isBefore(OffsetDateTime.now()) && savedTimestamp.isAfter(OffsetDateTime.now().minusMinutes(5)));
        priceRepository.flush();
        Optional<Price> persistedPrice = priceRepository.findById(emptyPrice.getId());
        assertTrue(persistedPrice.isPresent());
        persistedPrice.get().setPrice(BigDecimal.ZERO);
        priceRepository.saveAndFlush(persistedPrice.get());
        assertEquals(2, priceRepository.findAll().size());
        assertEquals(BigDecimal.ZERO, persistedPrice.get().getPrice().orElse(BigDecimal.ZERO));
        OffsetDateTime updatedTimestamp = persistedPrice.get().getUpdatedAt();
        assertNotNull(updatedTimestamp);
        assertTrue(updatedTimestamp.isBefore(OffsetDateTime.now()) && updatedTimestamp.isAfter(savedTimestamp));

    }


    @Test
    void uniqueConstraint__ok() {
        // -- allowed option-variant combinations within the same proposition
        Price linePrice = new Price(option, lineVariant);
        priceRepository.save(linePrice);

        Price marginPrice = new Price(option, marginVariant, BigDecimal.TEN);
        priceRepository.save(marginPrice);

        Option oddOption = new Option("key2", proposition, OptionType.ODD);
        oddOption.setName("Odd option");
        oddOption = optionRepository.save(oddOption);
        Price anotherOptionPrice = new Price(oddOption, lineVariant);
        priceRepository.saveAndFlush(anotherOptionPrice);
    }

    @Test
    void notNullConstraint__exceptionThrown() {

        assertThrows(NullPointerException.class, () -> new Price(null, lineVariant));
        assertThrows(NullPointerException.class, () -> new Price(option, null));
        assertThrows(NullPointerException.class, () -> new Price(null, lineVariant, BigDecimal.TEN));
        assertThrows(NullPointerException.class, () -> new Price(option, null, BigDecimal.TEN));
    }

    @Test
    void validateForeignOptionAndVariant_ok() {

        Proposition anotherProposition = createProposition(contest);

        final String optionKey = "key1111";
        option = new Option(optionKey, anotherProposition, OptionType.DRAW);
        option.setName("Name");
        option = optionRepository.saveAndFlush(option);

        assertThrows(IllegalArgumentException.class, () -> new Price(option, marginVariant));
        assertThrows(IllegalArgumentException.class, () -> new Price(option, marginVariant, BigDecimal.TEN));
    }

}
