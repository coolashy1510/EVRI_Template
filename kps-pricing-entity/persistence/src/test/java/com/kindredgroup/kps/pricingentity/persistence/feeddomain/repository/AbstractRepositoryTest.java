package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.config.RepositoryConfig;
import com.kindredgroup.kps.pricingentity.persistence.config.TestDataSourceConfig;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {RepositoryConfig.class, TestDataSourceConfig.class})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = {"/create.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = {"/drop.sql"})
@Transactional
public abstract class AbstractRepositoryTest {

    @Autowired protected ContestRepository contestRepository;
    @Autowired protected PropositionRepository propositionRepository;


    protected Contest createContest(ContestType type, ContestStatus status) {
        return contestRepository.save(TestHelper.createContest(status, type));
    }

    protected Proposition createProposition(Contest contest) {
        return propositionRepository.save(TestHelper.createProposition(contest));
    }

}
