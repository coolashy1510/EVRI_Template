package com.kindredgroup.kps.pricingentity.coreentity.logging;

import com.kindredgroup.commons.logging.common.CustomField;


public enum CoreEntityLoggingMarker implements CustomField {

    FIXTURE_KEY("fixtureKey"),
    TOURNAMENT_KEY("tournamentKey"),
    VENUE_KEY("venueKey"),
    TEAM_KEY("teamKey"),
    COMPETITION_KEY("competitionKey");

    private final String name;

    CoreEntityLoggingMarker(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
