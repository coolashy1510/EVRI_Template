package com.kindredgroup.kps.pricingentity.webapp.scheduler;

import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ArchiveContestRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ArchiveRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.ArchiveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArchiveSchedulerTest {
    private final ContestRepository contestRepository = mock(ContestRepository.class);
    private final ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
    private final ArchiveService archiveService = mock(ArchiveService.class);
    private final ArchiveContestRepository archiveContestRepository = mock(ArchiveContestRepository.class);
    ListAppender<ILoggingEvent> listAppender;
    private ArchiveScheduler archiveScheduler;
    long limit = 100;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(ArchiveScheduler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        archiveScheduler = new ArchiveScheduler(archiveService, contestRepository, archiveContestRepository, limit, limit);
    }

    @Test
    void archiveOutdatedData() {

        when(contestRepository.getExpired(limit)).thenReturn(List.of(123L, 456L));
        archiveScheduler.archiveOutdatedData();
        verify(contestRepository, times(1)).getExpired(limit);
        List.of(456L, 123L).forEach(contest -> verify(archiveService, times(1)).archive(contest));
        verifyNoMoreInteractions(archiveService);
        verifyNoMoreInteractions(contestRepository);
    }

    @Test
    void deleteArchivedData() {

        when(archiveContestRepository.getExpired(limit)).thenReturn(List.of(123L, 456L));
        archiveScheduler.deleteArchivedData();
        verify(archiveContestRepository, times(1)).getExpired(limit);
        List.of(456L, 123L).forEach(contest -> verify(archiveService, times(1)).delete(contest));
        verifyNoMoreInteractions(archiveService);
        verifyNoMoreInteractions(archiveContestRepository);
    }

    @Test
    void delete() {

        Long contestId = 1L;
        doAnswer(invocation -> {
            archiveRepository.deleteArchivedPrices(contestId);
            archiveRepository.deleteArchivedOutcomes(contestId);
            archiveRepository.deleteArchivedOptions(contestId);
            archiveRepository.deleteArchivedVariants(contestId);
            archiveRepository.deleteArchivedPlaceholders(contestId);
            archiveRepository.deleteArchivedPropositions(contestId);
            archiveRepository.deleteArchivedContest(contestId);
            return null;
        }).when(archiveService).delete(contestId);

        archiveService.delete(contestId);

        verify(archiveRepository, times(1)).deleteArchivedPrices(contestId);
        verify(archiveRepository, times(1)).deleteArchivedOutcomes(contestId);
        verify(archiveRepository, times(1)).deleteArchivedOptions(contestId);
        verify(archiveRepository, times(1)).deleteArchivedVariants(contestId);
        verify(archiveRepository, times(1)).deleteArchivedPlaceholders(contestId);
        verify(archiveRepository, times(1)).deleteArchivedPropositions(contestId);
        verify(archiveRepository, times(1)).deleteArchivedContest(contestId);
        verifyNoMoreInteractions(archiveRepository);

        verify(archiveService, times(1)).delete(contestId);
        verifyNoMoreInteractions(archiveService);
    }

    @Test
    void deleteArchivedData_shouldHandleException() {
        when(archiveContestRepository.getExpired(limit)).thenReturn(List.of(123L, 456L));
        doThrow(new RuntimeException("Database error")).when(archiveService).delete(123L);

        archiveScheduler.deleteArchivedData();

        verify(archiveContestRepository, times(1)).getExpired(limit);
        verify(archiveService, times(1)).delete(123L);
        verify(archiveService, times(1)).delete(456L);
        verifyNoMoreInteractions(archiveService);
        verifyNoMoreInteractions(archiveContestRepository);
        List<ILoggingEvent> infoLogs = listAppender.list.stream()
                                                        .filter(logEvent -> Level.INFO.equals(logEvent.getLevel()))
                                                        .toList();
        ILoggingEvent infoLog = infoLogs.getLast();
        String message = infoLog.getMessage();
        assertEquals(message, "Deleting Archived Records finished. Failed Contests: [123]");
    }

}

