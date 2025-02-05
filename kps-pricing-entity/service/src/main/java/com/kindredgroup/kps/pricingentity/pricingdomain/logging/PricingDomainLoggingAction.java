package com.kindredgroup.kps.pricingentity.pricingdomain.logging;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollectionEnriched;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.logging.LoggingActionImpl;

public class PricingDomainLoggingAction {
    public static LoggingActionImpl<Contest> PRODUCING_PRICING_DOMAIN_CONTEST = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_CONTEST");
    public static LoggingActionImpl<?> PRODUCING_TO_PRICING_DOMAIN_FAILED = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_CONTEST_FAILED");
    public static LoggingActionImpl<Proposition> PRODUCING_PRICING_DOMAIN_PROPOSITION = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_PROPOSITION");
   public static LoggingActionImpl<OptionChanged> PRODUCING_PRICING_DOMAIN_OPTION_CHANGED = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_OPTION_CHANGED");
    public static LoggingActionImpl<VariantChanged> PRODUCING_PRICING_DOMAIN_VARIANT_CHANGED = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_VARIANT_CHANGED");
    public static LoggingActionImpl<OutcomeResult> PRODUCING_PRICING_DOMAIN_OUTCOME_RESULT = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_OUTCOME_RESULT");
    public static LoggingActionImpl<PriceChangedCollectionEnriched> PRODUCING_PRICING_DOMAIN_PRICE_CHANGED_COLLECTION_ENRICHED = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_PRICE_CHANGED_COLLECTION_ENRICHED");
   public static LoggingActionImpl<PropositionChanged> PRODUCING_PRICING_DOMAIN_PROPOSITION_CHANGED = new LoggingActionImpl<>("PRODUCING_PRICING_DOMAIN_PROPOSITION_CHANGED");
}
