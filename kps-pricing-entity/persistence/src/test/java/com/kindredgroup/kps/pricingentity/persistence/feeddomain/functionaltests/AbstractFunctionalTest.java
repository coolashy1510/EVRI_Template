package com.kindredgroup.kps.pricingentity.persistence.feeddomain.functionaltests;

import com.kindredgroup.kps.pricingentity.persistence.config.RepositoryConfig;
import com.kindredgroup.kps.pricingentity.persistence.config.TestDataSourceConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {RepositoryConfig.class, TestDataSourceConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = {"/create.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = {"/drop.sql"})
public class AbstractFunctionalTest {
}
