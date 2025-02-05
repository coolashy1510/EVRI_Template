package com.kindredgroup.kps.pricingentity.coreentity.logging;

import com.kindredgroup.commons.logging.common.LoggingReason;

public enum CoreEntityLoggingReason implements LoggingReason {
    FIXTURE_KEY_NOT_FOUND_REASON_TEXT("FixtureKey not found by Entity Manager API"),
    COMPETITION_KEY_NOT_FOUND_REASON_TEXT("CompetitionKey not found by Entity Manager API"),
    TOURNAMENT_KEY_NOT_FOUND_REASON_TEXT("TournamentKey not found by Entity Manager API"),
    TEAM_KEY_NOT_FOUND_REASON_TEXT("TeamKey not found by Entity Manager API"),
    PARTICIPANT_KEY_NOT_FOUND_REASON_TEXT("ParticipantKey not found by Entity Manager API"),
    VENUE_KEY_NOT_FOUND_REASON_TEXT("VenueKey not found by Entity Manager API");

    private final String displayText;

    CoreEntityLoggingReason(final String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {

        return displayText;
    }
}
