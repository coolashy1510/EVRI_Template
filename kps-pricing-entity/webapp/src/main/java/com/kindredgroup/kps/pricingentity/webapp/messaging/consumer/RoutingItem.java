package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import jakarta.validation.constraints.NotBlank;

public record RoutingItem(@NotBlank String majorVersion, String outputTopic) {

}
