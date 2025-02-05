package com.kindredgroup.kps.pricingentity.coreentity.logging;

import java.util.List;
import java.util.Map;

import com.kindredgroup.commons.logging.common.CustomField;
import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Competition;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Team;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Venue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingAction.COMPETITION_KEY_NOT_FOUND;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingAction.FIXTURE_KEY_NOT_FOUND;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingAction.TEAM_KEY_NOT_FOUND;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingAction.TOURNAMENT_KEY_NOT_FOUND;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingAction.VENUE_KEY_NOT_FOUND;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingMarker.COMPETITION_KEY;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingMarker.FIXTURE_KEY;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingMarker.TEAM_KEY;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingMarker.TOURNAMENT_KEY;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingMarker.VENUE_KEY;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.COMPETITION_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.FIXTURE_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.TEAM_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.TOURNAMENT_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.VENUE_KEY_NOT_FOUND_REASON_TEXT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class CoreEntityLoggerTest {

    public static final String TEST_FIXTURE_KEY = "fixtureKey";
    public static final String TEST_COMPETITION_KEY = "competitionKey";
    public static final String TEST_TOURNAMENT_KEY = "tournamentKey";
    public static final String TEST_VENUE_KEY = "venueKey";
    public static final String TEST_TEAM_KEY = "teamKey";
    @Mock
    private KpsLogger kpsLogger;

    private CoreEntityLogger coreEntityLogger;

    @Captor
    ArgumentCaptor<Map<CustomField, Object>> parametersCaptor;
    private AutoCloseable closeable;
    private MockedStatic<KpsLoggerFactory> mockedFactory;



    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockedFactory = mockStatic(KpsLoggerFactory.class);
        mockedFactory.when(() -> KpsLoggerFactory.getLogger(any(Logger.class))).thenReturn(kpsLogger);
        coreEntityLogger = new CoreEntityLogger();
    }

    @AfterEach
    void terminate() throws Exception {
        closeable.close();
        mockedFactory.close();
    }

    @Test
    void logFixtureNotFoundWarning() {
        coreEntityLogger.logFixtureNotFoundWarning(TEST_FIXTURE_KEY);
        verify(kpsLogger).warn(eq(FIXTURE_KEY_NOT_FOUND), eq(FIXTURE_KEY_NOT_FOUND_REASON_TEXT), parametersCaptor.capture());
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertEquals(TEST_FIXTURE_KEY, actual.get(FIXTURE_KEY));
    }

    @Test
    void logCompetitionNotFoundWarning() {
        Fixture fixture = Fixture.builder().key(TEST_FIXTURE_KEY).competition(Competition.builder().key(TEST_COMPETITION_KEY).build()).build();
        coreEntityLogger.logCompetitionNotFoundWarning(fixture);
        verify(kpsLogger).warn(eq(COMPETITION_KEY_NOT_FOUND), eq(COMPETITION_KEY_NOT_FOUND_REASON_TEXT), parametersCaptor.capture());
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(TEST_FIXTURE_KEY, actual.get(FIXTURE_KEY)),
                () -> assertEquals(TEST_COMPETITION_KEY, actual.get(COMPETITION_KEY))
        );
    }

    @Test
    void logTournamentNotFoundWarning() {
        Fixture fixture = Fixture.builder().key(TEST_FIXTURE_KEY).tournament(Tournament.builder().key(TEST_TOURNAMENT_KEY).build()).build();
        coreEntityLogger.logTournamentNotFoundWarning(fixture);
        verify(kpsLogger).warn(eq(TOURNAMENT_KEY_NOT_FOUND), eq(TOURNAMENT_KEY_NOT_FOUND_REASON_TEXT), parametersCaptor.capture());
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(TEST_FIXTURE_KEY, actual.get(FIXTURE_KEY)),
                () -> assertEquals(TEST_TOURNAMENT_KEY, actual.get(TOURNAMENT_KEY))
        );
    }

    @Test
    void logVenueNotFoundWarning() {
        Fixture fixture = Fixture.builder().key(TEST_FIXTURE_KEY).venue(Venue.builder().key(TEST_VENUE_KEY).build()).build();
        coreEntityLogger.logVenueNotFoundWarning(fixture);
        verify(kpsLogger).warn(eq(VENUE_KEY_NOT_FOUND), eq(VENUE_KEY_NOT_FOUND_REASON_TEXT), parametersCaptor.capture());
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(TEST_FIXTURE_KEY, actual.get(FIXTURE_KEY)),
                () -> assertEquals(TEST_VENUE_KEY, actual.get(VENUE_KEY))
        );
    }

    @Test
    void logTeamsNotFoundWarning() {
        Fixture fixture = Fixture.builder().key(TEST_FIXTURE_KEY).teams(List.of(Team.builder().key(TEST_TEAM_KEY).build())).build();
        coreEntityLogger.logTeamsNotFoundWarning(fixture);
        verify(kpsLogger).warn(eq(TEAM_KEY_NOT_FOUND), eq(TEAM_KEY_NOT_FOUND_REASON_TEXT), parametersCaptor.capture());
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(TEST_FIXTURE_KEY, actual.get(FIXTURE_KEY)),
                () -> assertEquals(TEST_TEAM_KEY, actual.get(TEAM_KEY))
        );
    }
}
