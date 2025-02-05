package com.kindredgroup.kps.pricingentity.coreentity.logging;

import com.kindredgroup.commons.logging.common.LoggingAction;

public enum CoreEntityLoggingAction implements LoggingAction {
    COMPETITION_UPDATED,
    COMPETITION_NOTHING_TO_UPDATED,
    COMPETITION_SAVED,
    TOURNAMENT_UPDATED,
    TOURNAMENT_NOTHING_TO_UPDATED,
    TOURNAMENT_SAVED,
    FIXTURE_KEY_NOT_FOUND,
    COMPETITION_KEY_NOT_FOUND,
    VENUE_KEY_NOT_FOUND,
    TEAM_KEY_NOT_FOUND,
    TOURNAMENT_KEY_NOT_FOUND;

    CoreEntityLoggingAction() {
    }
}
