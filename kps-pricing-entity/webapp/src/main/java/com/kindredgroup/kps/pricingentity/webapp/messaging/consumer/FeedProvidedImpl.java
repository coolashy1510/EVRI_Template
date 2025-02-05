package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvided;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;

@JsonIgnoreProperties(ignoreUnknown = true)
record FeedProvidedImpl(FeedProvider provider) implements FeedProvided {
}
