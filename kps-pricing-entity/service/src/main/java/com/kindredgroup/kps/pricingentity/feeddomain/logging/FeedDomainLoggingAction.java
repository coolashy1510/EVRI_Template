package com.kindredgroup.kps.pricingentity.feeddomain.logging;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.logging.LoggingActionImpl;

public final class FeedDomainLoggingAction {
    public static LoggingActionImpl<Contest> RECEIVING_FEED_DOMAIN_CONTEST = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_CONTEST");
    public static LoggingActionImpl<Proposition> RECEIVING_FEED_DOMAIN_PROPOSITION = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_PROPOSITION");
    public static LoggingActionImpl<OptionChanged> RECEIVING_FEED_DOMAIN_OPTION_CHANGED = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_OPTION_CHANGED");
    public static LoggingActionImpl<VariantChanged> RECEIVING_FEED_DOMAIN_VARIANT_CHANGED = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_VARIANT_CHANGED");
    public static LoggingActionImpl<OutcomeResult> RECEIVING_FEED_DOMAIN_OUTCOME_RESULT = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_OUTCOME_RESULT");
    public static LoggingActionImpl<PriceChangedCollection> RECEIVING_FEED_DOMAIN_PRICE_CHANGED_COLLECTION = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_PRICE_CHANGED_COLLECTION");
    public static LoggingActionImpl<PropositionChanged> RECEIVING_FEED_DOMAIN_PROPOSITION_CHANGED = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_PROPOSITION_CHANGED");
    public static LoggingActionImpl<Object> ERROR_RECEIVING_FEED_DOMAIN_MESSAGE = new LoggingActionImpl<>("ERROR_RECEIVING_FEED_DOMAIN_MESSAGE");
    public static LoggingActionImpl<Object> RECEIVING_FEED_DOMAIN_MESSAGE = new LoggingActionImpl<>("RECEIVING_FEED_DOMAIN_MESSAGE");
    public static LoggingActionImpl<Object> PRODUCING_PRICING_DOMAIN_MESSAGE = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_MESSAGE");

}
