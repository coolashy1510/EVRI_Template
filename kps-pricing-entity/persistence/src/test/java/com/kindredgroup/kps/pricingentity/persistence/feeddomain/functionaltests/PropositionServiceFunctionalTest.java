package com.kindredgroup.kps.pricingentity.persistence.feeddomain.functionaltests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.internal.api.pricingdomain.Entity;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Option;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.OptionEntity;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.OptionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.VariantRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.ContestServiceImpl;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.PropositionServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class PropositionServiceFunctionalTest extends AbstractFunctionalTest {

    @Autowired protected ContestRepository contestRepository;
    @Autowired protected PropositionRepository propositionRepository;

    @Autowired
    private OptionRepository optionRepository;
    @Autowired private VariantRepository variantRepository;

    private PropositionServiceImpl propositionService;

    @BeforeEach
    void setUp() {
        ContestServiceImpl contestService = new ContestServiceImpl(contestRepository);
        propositionService = new PropositionServiceImpl(contestService, propositionRepository);

    }

    @Test
    void get_ok() {

        Contest inPlay = TestHelper.createContest(ContestStatus.IN_PLAY, ContestType.POOL);
        contestRepository.save(inPlay);

        Contest preGame = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.POOL);
        contestRepository.save(preGame);

        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition entity =
                new com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition();
        entity.setName("proposition name");
        entity.setContest(preGame);
        entity.setType("type");
        entity.setKey("proposition_key");
        propositionRepository.save(entity);

        assertTrue(propositionService.get(inPlay, "proposition_key").isEmpty());
        assertTrue(propositionService.get(preGame, "proposition_key").isPresent());
        assertTrue(propositionService.get(preGame, "another_proposition_key").isEmpty());
    }

    @Test
    void save_ok() {

        Contest contest = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.POOL);
        contestRepository.save(contest);

        List<Option> options = new ArrayList<>();
        options.add(Option.builder().optionKey("optionKey").optionType(OptionType.T1).name("optionName").build());
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.builder().variantKey("variantKey").variantType(VariantType.LINE).name("variantName").build());

        Proposition payload = Proposition.builder().propositionType("total").name("Football simple").propositionKey("key")
                                         .contestKey(contest.getKey()).options(options).variants(variants).build();

        propositionService.save(payload);

        final Optional<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition> propositionEntity =
                propositionService.get(
                        contest, "key");

        assertTrue(propositionEntity.isPresent());
        var proposition = propositionEntity.get();
        assertEquals("key", proposition.getKey());
        assertEquals("Football simple", proposition.getName());
        assertEquals("total", proposition.getType());
        assertEquals(contest, proposition.getContest());

        assertEquals(1, proposition.getOptions().size());
        assertEquals(1, proposition.getVariants().size());
        assertTrue(proposition.getPlaceholders().isEmpty());
        var option = optionRepository.findByPropositionAndKey(proposition, "optionKey").orElseThrow();
        var variant = variantRepository.findByPropositionAndKey(proposition, "variantKey").orElseThrow();
        assertEquals(option, proposition.getOptions().get(0));
        assertEquals(variant, proposition.getVariants().get(0));

        assertEquals(proposition, option.getProposition());
        assertEquals(OptionType.T1, option.getType());
        assertEquals("optionKey", option.getKey());
        assertEquals("optionName", option.getName());

        assertEquals(proposition, variant.getProposition());
        assertEquals(VariantType.LINE, variant.getType());
        assertEquals("variantKey", variant.getKey());
        assertEquals("variantName", variant.getName());

    }

    @Test
    void save_withPlaceholders_ok() {

        Contest contest = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.POOL);
        contestRepository.save(contest);

        List<Option> options = new ArrayList<>();
        options.add(Option.builder().optionKey("optionKey").optionType(OptionType.T1).name("optionName").build());
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.builder().variantKey("variantKey").variantType(VariantType.LINE).name("variantName").build());

        Proposition payload = Proposition.builder().propositionType("total").name("Football simple").propositionKey("key")
                                         .contestKey(contest.getKey()).options(options).variants(variants)
                                         .placeholders(Map.of("placeholderName", "placeholderValue")).build();

        propositionService.save(payload);

        final Optional<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition> propositionEntity =
                propositionService.get(
                        contest, "key");

        assertTrue(propositionEntity.isPresent());
        var proposition = propositionEntity.get();
        assertEquals("key", proposition.getKey());
        assertEquals("Football simple", proposition.getName());
        assertEquals("total", proposition.getType());
        assertEquals(contest, proposition.getContest());

        assertEquals(1, proposition.getOptions().size());
        assertEquals(1, proposition.getVariants().size());
        assertEquals(1, proposition.getPlaceholders().size());
        var option = optionRepository.findByPropositionAndKey(proposition, "optionKey").orElseThrow();
        var variant = variantRepository.findByPropositionAndKey(proposition, "variantKey").orElseThrow();
        assertEquals(option, proposition.getOptions().get(0));
        assertEquals(variant, proposition.getVariants().get(0));

        assertEquals(proposition, option.getProposition());
        assertEquals(OptionType.T1, option.getType());
        assertEquals("optionKey", option.getKey());
        assertEquals("optionName", option.getName());

        assertEquals(proposition, variant.getProposition());
        assertEquals(VariantType.LINE, variant.getType());
        assertEquals("variantKey", variant.getKey());
        assertEquals("variantName", variant.getName());

        assertEquals("placeholderName", proposition.getPlaceholders().get(0).getName());
        assertEquals("placeholderValue", proposition.getPlaceholders().get(0).getValue());

    }

    @Test
    void save_withOptionEntity_ok() {

        Contest contest = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.POOL);
        contestRepository.save(contest);

        List<Option> options = new ArrayList<>();
        List<Entity> entities = List.of(new Entity("50Q60OCU19YJMCHELQBC1OFBC", "player_key"),
                new Entity("T49MD3M2F44JXLK1TS1PGMFL", "player_key"));
        options.add(
                Option.builder().optionKey("optionKey").optionType(OptionType.PARTICIPANT).name("optionName").entities(entities)
                      .build());
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.builder().variantKey("variantKey").variantType(VariantType.PLAIN).name("variantName").build());

        Proposition payload = Proposition.builder().propositionType("total").name("Football simple").propositionKey("key")
                                         .contestKey(contest.getKey()).options(options).variants(variants)
                                         .placeholders(Map.of("placeholderName", "placeholderValue")).build();

        propositionService.save(payload);

        final Optional<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition> propositionEntity =
                propositionService.get(
                        contest, "key");

        assertTrue(propositionEntity.isPresent());
        var proposition = propositionEntity.get();
        assertEquals("key", proposition.getKey());
        assertEquals("Football simple", proposition.getName());
        assertEquals("total", proposition.getType());
        assertEquals(contest, proposition.getContest());

        assertEquals(1, proposition.getOptions().size());
        assertEquals(1, proposition.getVariants().size());
        assertEquals(1, proposition.getPlaceholders().size());
        var option = optionRepository.findByPropositionAndKey(proposition, "optionKey").orElseThrow();
        var variant = variantRepository.findByPropositionAndKey(proposition, "variantKey").orElseThrow();
        assertEquals(option, proposition.getOptions().get(0));
        assertEquals(variant, proposition.getVariants().get(0));

        assertEquals(proposition, option.getProposition());
        assertEquals(OptionType.PARTICIPANT, option.getType());
        assertEquals("optionKey", option.getKey());
        assertEquals("optionName", option.getName());

        var optionEntities = option.getOptionEntities();
        assertEquals(entities.size(), optionEntities.size());
        entities.forEach(entity -> assertEquals(entity.entityType(),
                optionEntities.stream().filter(optionEntity -> optionEntity.getKey().equals(entity.key())).findAny()
                              .orElseThrow().getType()));
        assertEquals(entities.stream().map(Entity::key).toList(),
                option.getOptionEntities().stream().map(OptionEntity::getKey).toList());

        assertEquals(proposition, variant.getProposition());
        assertEquals(VariantType.PLAIN, variant.getType());
        assertEquals("variantKey", variant.getKey());
        assertEquals("variantName", variant.getName());

        assertEquals("placeholderName", proposition.getPlaceholders().get(0).getName());
        assertEquals("placeholderValue", proposition.getPlaceholders().get(0).getValue());

    }

}
