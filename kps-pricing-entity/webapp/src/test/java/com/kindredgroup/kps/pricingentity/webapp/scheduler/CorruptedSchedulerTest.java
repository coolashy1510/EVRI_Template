package com.kindredgroup.kps.pricingentity.webapp.scheduler;

import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorruptedSchedulerTest {
    private final ContestRepository contestRepository = mock(ContestRepository.class);

    private CorruptedScheduler corruptedScheduler;

    @BeforeEach
    void setUp() {
        corruptedScheduler = new CorruptedScheduler(contestRepository, mock(MetricsHelper.class));
    }

    @Test
    void archiveOutdatedData() {
        when(contestRepository.getCorrupted()).thenReturn(List.of(4L,1L));
        corruptedScheduler.getCorruptedData();
        verify(contestRepository, times(1)).getCorrupted();
        verifyNoMoreInteractions(contestRepository);
    }

}
