package com.kindredgroup.kps.pricingentity.coreentity.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.kindredgroup.commons.logging.common.CustomField;
import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.kindredgroup.commons.logging.common.CustomField.NOT_SET;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingAction.*;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingMarker.*;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.*;

@Component
public class CoreEntityLogger {

    public void logFixtureNotFoundWarning(String fixtureKey) {
        final Map<CustomField, Object> loggingDimensions = new HashMap<>();
        loggingDimensions.put(FIXTURE_KEY, Objects.toString(fixtureKey, NOT_SET));
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.warn(FIXTURE_KEY_NOT_FOUND, FIXTURE_KEY_NOT_FOUND_REASON_TEXT, loggingDimensions);

    }

    public void logCompetitionNotFoundWarning(Fixture fixture) {
        final Map<CustomField, Object> loggingDimensions = new HashMap<>();
        loggingDimensions.put(FIXTURE_KEY, Objects.toString(fixture.key(), NOT_SET));
        loggingDimensions.put(COMPETITION_KEY, Optional.ofNullable(fixture.competition())
                                                       .map(Competition::key)
                                                       .orElse(NOT_SET));
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.warn(COMPETITION_KEY_NOT_FOUND, COMPETITION_KEY_NOT_FOUND_REASON_TEXT, loggingDimensions);
    }

    public void logTournamentNotFoundWarning(Fixture fixture) {
        final Map<CustomField, Object> loggingDimensions = new HashMap<>();
        loggingDimensions.put(FIXTURE_KEY, Objects.toString(fixture.key(), NOT_SET));
        loggingDimensions.put(TOURNAMENT_KEY, Optional.ofNullable(fixture.tournament())
                                                       .map(Tournament::key)
                                                       .orElse(NOT_SET));
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.warn(TOURNAMENT_KEY_NOT_FOUND, TOURNAMENT_KEY_NOT_FOUND_REASON_TEXT, loggingDimensions);
    }

    public void logVenueNotFoundWarning(Fixture fixture) {
        final Map<CustomField, Object> loggingDimensions = new HashMap<>();
        loggingDimensions.put(FIXTURE_KEY, Objects.toString(fixture.key(), NOT_SET));
        loggingDimensions.put(VENUE_KEY, Optional.ofNullable(fixture.venue())
                .map(Venue::key)
                .orElse(NOT_SET));
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.warn(VENUE_KEY_NOT_FOUND, VENUE_KEY_NOT_FOUND_REASON_TEXT, loggingDimensions);
    }

    public void logTeamsNotFoundWarning(Fixture fixture) {
        final Map<CustomField, Object> loggingDimensions = new HashMap<>();
        loggingDimensions.put(FIXTURE_KEY, Objects.toString(fixture.key(), NOT_SET));
        fixture.teams().forEach(t -> loggingDimensions.put(TEAM_KEY, Optional.ofNullable(t)
                .map(Team::key)
                .orElse(NOT_SET)));
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.warn(TEAM_KEY_NOT_FOUND, TEAM_KEY_NOT_FOUND_REASON_TEXT, loggingDimensions);
    }


}
