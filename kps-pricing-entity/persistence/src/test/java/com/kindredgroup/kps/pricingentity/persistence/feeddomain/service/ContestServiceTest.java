package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.util.Optional;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContestServiceTest {
    private static final com.kindredgroup.kps.internal.api.pricingdomain.Contest CONTEST = TestHelper.CONTEST_PAYLOAD;
    public static final String CONTEST_NAME = "contestName";
    public static final String CONTEST_KEY = "12245b7ae4b58d21f6e395229b43f1e7";
    private final ContestRepository contestRepository = mock(ContestRepository.class);

    private Contest contestDb;
    @Captor
    ArgumentCaptor<Contest> contestCaptor;
    private ContestServiceImpl contestService;

    @BeforeEach
    void setUp() {
        contestService = new ContestServiceImpl(contestRepository);
        contestDb = new Contest();
        contestDb.setKey(CONTEST_KEY);
        contestDb.setType(ContestType.ATHLETICS);
        contestDb.setName(CONTEST_NAME);
        contestDb.setStatus(ContestStatus.CONCLUDED);
        contestDb.setKey(CONTEST_KEY);
    }

    @Test
    void save_present_updated() {

        when(contestRepository.findByKey(CONTEST_KEY)).thenReturn(Optional.of(contestDb));
        when(contestRepository.save(any())).thenReturn(contestDb);
        contestService.save(CONTEST);

        verify(contestRepository, times(1)).save(contestCaptor.capture());

        assertFalse(contestCaptor.getValue().getKey().isBlank());
        assertEquals(contestDb.getKey(), contestCaptor.getValue().getKey());
        assertEquals(contestDb.getName(), contestCaptor.getValue().getName());
        assertEquals(contestDb.getType(), contestCaptor.getValue().getType());
        assertEquals(contestDb.getStatus(), contestCaptor.getValue().getStatus());
    }

    @Test
    void getByKey_ok() {
        when(contestRepository.findByKey("test")).thenReturn(Optional.of(contestDb));
        Optional<com.kindredgroup.kps.internal.api.pricingdomain.Contest> result = contestService.getByKey("test");
        assertTrue(result.isPresent());
        assertAll(
                () -> assertEquals(CONTEST_KEY, result.get().contestKey()),
                () -> assertEquals(contestDb.getStatus(), result.get().status()),
                () -> assertEquals(ContestType.ATHLETICS.getValue(), result.get().contestType()),
                () -> assertEquals(CONTEST_NAME, result.get().name())
        );
    }

    @Test
    void save_notPresent_created() {

        when(contestRepository.findByKey(any())).thenReturn(Optional.empty());
        when(contestRepository.save(any())).thenReturn(contestDb);

        contestService.save(CONTEST);

        verify(contestRepository, times(1)).findByKey(CONTEST.contestKey());
        verify(contestRepository, times(1)).save(contestCaptor.capture());

        assertFalse(contestCaptor.getValue().getKey().isBlank());
        assertEquals(CONTEST.name(), contestCaptor.getValue().getName());
        assertEquals(CONTEST.contestType(), contestCaptor.getValue().getType().getValue());
        assertEquals(CONTEST.status(), contestCaptor.getValue().getStatus());
        assertEquals(CONTEST.contestKey(), contestCaptor.getValue().getKey());

        verifyNoMoreInteractions(contestRepository);

    }

    @Test
    void getContestType_ok() {
        when(contestRepository.getContestTypeByKey("anyKey")).thenReturn(Optional.of(ContestType.FOOTBALL));
        Optional<ContestType> result = contestService.getContestType("anyKey");
        assertTrue(result.isPresent());
        assertEquals(ContestType.FOOTBALL, result.get());
    }

    @Test
    void getContestType_empty() {
        when(contestRepository.getContestTypeByKey("wrongKey")).thenReturn(Optional.empty());
        Optional<ContestType> result = contestService.getContestType("wrongKey");
        assertFalse(result.isPresent());
        assertEquals(Optional.empty(), result);
    }

}
