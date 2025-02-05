package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ArchiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveServiceTest {

    private final ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
    private ArchiveService archiveService;

    @BeforeEach
    void setUp() {
        archiveService = new ArchiveService(archiveRepository);
    }

    @Test
    void archive() {
        Contest contest1 = mock(Contest.class);
        when(contest1.getId()).thenReturn(1L);
        archiveService.archive(contest1.getId());
        verify(archiveRepository, times(1)).archivePrices(contest1.getId());
        verify(archiveRepository, times(1)).archiveVariants(contest1.getId());
        verify(archiveRepository, times(1)).archiveOptions(contest1.getId());
        verify(archiveRepository, times(1)).archivePlaceholders(contest1.getId());
        verify(archiveRepository, times(1)).archiveOutcomes(contest1.getId());
        verify(archiveRepository, times(1)).archivePropositions(contest1.getId());
        verify(archiveRepository, times(1)).archiveContest(contest1.getId());
        verify(archiveRepository, times(1)).deletePrices(contest1.getId());
        verify(archiveRepository, times(1)).deleteVariants(contest1.getId());
        verify(archiveRepository, times(1)).deleteOptions(contest1.getId());
        verify(archiveRepository, times(1)).deletePlaceholders(contest1.getId());
        verify(archiveRepository, times(1)).deleteOutcomes(contest1.getId());
        verify(archiveRepository, times(1)).deletePropositions(contest1.getId());
        verify(archiveRepository, times(1)).deleteContest(contest1.getId());
        verifyNoMoreInteractions(archiveRepository);
    }
}
