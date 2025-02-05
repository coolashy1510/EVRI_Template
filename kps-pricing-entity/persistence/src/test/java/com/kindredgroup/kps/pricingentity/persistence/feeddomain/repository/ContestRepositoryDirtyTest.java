package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.config.RepositoryConfig;
import com.kindredgroup.kps.pricingentity.persistence.config.TestDataSourceConfig;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {RepositoryConfig.class, TestDataSourceConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = {"/create.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = {"/drop.sql"})
class ContestRepositoryDirtyTest {

    @Autowired
    ContestRepository contestRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void getExpired_ok() {
        Contest contest1 = TestHelper.createContest(ContestStatus.CONCLUDED, ContestType.FOOTBALL);
        contestRepository.save(contest1);
        Contest contest2 = TestHelper.createContest(ContestStatus.CONCLUDED, ContestType.FOOTBALL);
        contestRepository.save(contest2);
        Contest contest3 = contestRepository.save(TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.FOOTBALL));
        Contest contest4 = contestRepository.save(TestHelper.createContest(ContestStatus.SUSPENDED, ContestType.FOOTBALL));
        assertEquals(0, contestRepository.getExpired(5).size());
        jdbcTemplate.update("update pricing_entity.contest set updated_at = now() - interval '8' day where key=?;",
                contest1.getKey());
        jdbcTemplate.update("update pricing_entity.contest set updated_at = now() - interval '8' day where key=?;",
                contest2.getKey());
        jdbcTemplate.update("update pricing_entity.contest set updated_at = now() - interval '8' day where key=?;",
                contest3.getKey());
        jdbcTemplate.update("update pricing_entity.contest set updated_at = now() - interval '8' day where key=?;",
                contest4.getKey());
        assertEquals(3, contestRepository.getExpired(5).size());
        assertEquals(1, contestRepository.getExpired(1).size());
    }

    @Test
    void getCorrupted_ok() {
        Contest contest1 = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.FOOTBALL);
        contestRepository.save(contest1);
        Contest contest2 = TestHelper.createContest(ContestStatus.IN_PLAY, ContestType.FOOTBALL);
        contestRepository.save(contest2);
        Contest contest3 = contestRepository.save(TestHelper.createContest(ContestStatus.CONCLUDED, ContestType.FOOTBALL));
        Contest contest4 = contestRepository.save(TestHelper.createContest(ContestStatus.SUSPENDED, ContestType.FOOTBALL));
        assertEquals(0, contestRepository.getCorrupted().size());
        jdbcTemplate.update("update pricing_entity.contest set start_date_time = now() - interval '15' day where key=?;",
                contest1.getKey());
        jdbcTemplate.update("update pricing_entity.contest set start_date_time = now() - interval '15' day where key=?;",
                contest2.getKey());
        jdbcTemplate.update("update pricing_entity.contest set start_date_time = now() - interval '15' day where key=?;",
                contest3.getKey());
        jdbcTemplate.update("update pricing_entity.contest set start_date_time = now() - interval '15' day where key=?;",
                contest4.getKey());
        assertEquals(2, contestRepository.getCorrupted().size());
    }
}
