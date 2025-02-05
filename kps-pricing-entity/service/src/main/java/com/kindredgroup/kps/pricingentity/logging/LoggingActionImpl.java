package com.kindredgroup.kps.pricingentity.logging;

import com.kindredgroup.commons.logging.common.LoggingAction;


public class LoggingActionImpl<T> implements LoggingAction {

    private final String displayName;

    public LoggingActionImpl(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
