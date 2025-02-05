package com.kindredgroup.kps.pricingentity.persistence.feeddomain.functionaltests;

import java.util.List;
import java.util.Map;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.PropositionPlaceholder;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ArchiveRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.OptionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.VariantRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.ArchiveService;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.ContestServiceImpl;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.PropositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class ArchiveServiceFunctionalTest extends AbstractFunctionalTest {

    @Autowired protected ContestRepository contestRepository;
    @Autowired protected ArchiveRepository archiveRepository;
    @Autowired protected PropositionRepository propositionRepository;
    @Autowired protected OptionRepository optionRepository;
    @Autowired protected VariantRepository variantRepository;
    @Autowired protected JdbcTemplate jdbcTemplate;
    @Autowired protected PlatformTransactionManager transactionManager;
    private ArchiveService archiveService;
    private ArchiveService archiveServiceWithFakeArchiveRepo;
    private PropositionServiceImpl propositionService;
    private com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition proposition;
    private ArchiveRepository fakeArchiveRepository = mock(ArchiveRepository.class);
    private TransactionTemplate transactionTemplate;

    private Contest create() {
        var contest = TestHelper.createContest(ContestStatus.CONCLUDED, ContestType.POOL);
        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition p =
                new com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition();
        p.setContest(contest);
        p.setType("total");
        p.setName("Football simple");
        p.setKey("prop_key");
        final PropositionPlaceholder ph = new PropositionPlaceholder();
        ph.setValue("placeholderValue");
        ph.setName("placeholderName");
        ph.setProposition(p);
        p.getPlaceholders().add(ph);
        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option o =
                new com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option("optionKey", p, OptionType.T1);
        o.setName("Option Name");
        p.getOptions().add(o);
        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant v =
                new com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant("variantKey", p, VariantType.LINE);
        v.setName("Variant Name");
        p.getVariants().add(v);
        contest.getPropositions().add(p);
        contest = contestRepository.saveAndFlush(contest);


        proposition = propositionService.get(contest, "prop_key").orElseThrow();
        optionRepository.findByPropositionAndKey(proposition, "optionKey").orElseThrow();
        variantRepository.findByPropositionAndKey(proposition, "variantKey").orElseThrow();
        return contest;
    }

    @BeforeEach
    void setUp() {
        archiveService = new ArchiveService(archiveRepository);
        archiveServiceWithFakeArchiveRepo = new ArchiveService(fakeArchiveRepository);
        ContestServiceImpl contestService = new ContestServiceImpl(contestRepository);
        propositionService = new PropositionServiceImpl(contestService, propositionRepository);
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void archiveOutdatedData_ok() {
        Contest contest = create();
        var propositions = propositionRepository.findAll();
        var variants = variantRepository.findAll();
        var options = optionRepository.findAll();
        final List<Contest> contests = contestRepository.findAll();
        assertEquals(1, contests.size());
        assertEquals(1, propositions.size());
        final List<Map<String, Object>> placeholders = jdbcTemplate.queryForList(
                "select * from pricing_entity.proposition_placeholder");
        assertEquals(1, placeholders.size());
        assertEquals(1, variants.size());
        assertEquals(1, options.size());

        archiveService.archive(contest.getId());
        assertTrue(contestRepository.findAll().isEmpty());
        assertTrue(propositionRepository.findAll().isEmpty());
        assertTrue(variantRepository.findAll().isEmpty());
        assertTrue(optionRepository.findAll().isEmpty());

        final List<Map<String, Object>> archivedContests = jdbcTemplate.queryForList(
                "select * from pricing_entity.archived_contest");
        assertEquals(1, archivedContests.size());
        assertAll(() -> assertEquals(contest.getId(), archivedContests.get(0).get("id")),
                () -> assertEquals(contest.getType().getValue(), archivedContests.get(0).get("type")),
                () -> assertEquals(contest.getName(), archivedContests.get(0).get("name")),
                () -> assertEquals(contest.getStatus().getValue(), archivedContests.get(0).get("status")),
                () -> assertEquals(contest.getKey(), archivedContests.get(0).get("key")));
        final List<Map<String, Object>> archivedPropositions = jdbcTemplate.queryForList(
                "select * from pricing_entity.archived_proposition");
        assertEquals(1, archivedPropositions.size());

        assertAll(() -> assertEquals(proposition.getId(), archivedPropositions.get(0).get("id")),
                () -> assertEquals(proposition.getType(), archivedPropositions.get(0).get("type")),
                () -> assertEquals(proposition.getName(), archivedPropositions.get(0).get("name")),
                () -> assertEquals(contest.getId(), archivedPropositions.get(0).get("contest_id")),
                () -> assertEquals(proposition.getKey(), archivedPropositions.get(0).get("key")));

        final List<Map<String, Object>> archivedVariants = jdbcTemplate.queryForList(
                "select * from pricing_entity.archived_variant");
        assertEquals(1, archivedVariants.size());
        assertAll(() -> assertEquals(variants.get(0).getId(), archivedVariants.get(0).get("id")),
                () -> assertEquals(variants.get(0).getType().getValue(), archivedVariants.get(0).get("type")),
                () -> assertEquals(variants.get(0).getName(), archivedVariants.get(0).get("name")),
                () -> assertEquals(proposition.getId(), archivedVariants.get(0).get("proposition_id")),
                () -> assertEquals(variants.get(0).getKey(), archivedVariants.get(0).get("key")));

        final List<Map<String, Object>> archivedOptions = jdbcTemplate.queryForList(
                "select * from pricing_entity.archived_option");
        assertEquals(1, archivedOptions.size());
        assertAll(() -> assertEquals(options.get(0).getId(), archivedOptions.get(0).get("id")),
                () -> assertEquals(options.get(0).getType().getValue(), archivedOptions.get(0).get("type")),
                () -> assertEquals(options.get(0).getName(), archivedOptions.get(0).get("name")),
                () -> assertEquals(proposition.getId(), archivedOptions.get(0).get("proposition_id")),
                () -> assertEquals(options.get(0).getKey(), archivedOptions.get(0).get("key")));

        final List<Map<String, Object>> archivedPlaceholders = jdbcTemplate.queryForList(
                "select * from pricing_entity.archived_proposition_placeholder");
        assertEquals(1, archivedPlaceholders.size());
        assertAll(() -> assertEquals(placeholders.get(0).get("id"), archivedPlaceholders.get(0).get("id")),
                () -> assertEquals(proposition.getId(), archivedPlaceholders.get(0).get("proposition_id")),
                () -> assertEquals(placeholders.get(0).get("name"),
                        archivedPlaceholders.get(0).get("name")),
                () -> assertEquals(placeholders.get(0).get("value"),
                        archivedPlaceholders.get(0).get("value")));
    }

    @Test
    void archiveOutdatedData_transactionFailed_ok() {
        // create real contest in the DB
        Contest contest = create();
        final List<Contest> contests = contestRepository.findAll();
        assertEquals(1, contests.size());

        final List<Map<String, Object>> archivedContests = jdbcTemplate.queryForList(
                "select * from pricing_entity.archived_contest");
        assertTrue(archivedContests.isEmpty());

        doThrow(new NullPointerException("test")).when(fakeArchiveRepository).deleteContest(contest.getId());
        doCallRealMethod().when(fakeArchiveRepository).archivePrices(anyLong());
        doCallRealMethod().when(fakeArchiveRepository).archiveOptions(anyLong());
        doCallRealMethod().when(fakeArchiveRepository).archiveVariants(anyLong());
        doCallRealMethod().when(fakeArchiveRepository).archivePlaceholders(anyLong());
        doCallRealMethod().when(fakeArchiveRepository).archivePropositions(anyLong());
        doCallRealMethod().when(fakeArchiveRepository).archiveContest(anyLong());
        try {
            transactionTemplate.execute(status -> {
                archiveServiceWithFakeArchiveRepo.archive(contest.getId());
                return null;
            });
        } catch (NullPointerException ignored) { }

        assertEquals(1, contestRepository.findAll().size());
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from pricing_entity.contest", Long.class));

        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from pricing_entity.archived_contest", Long.class));
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from pricing_entity.archived_proposition", Long.class));
        assertEquals(0,
                jdbcTemplate.queryForObject("select count(*) from pricing_entity.archived_proposition_placeholder", Long.class));
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from pricing_entity.archived_variant", Long.class));
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from pricing_entity.archived_option", Long.class));
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from pricing_entity.archived_price", Long.class));


    }
}

