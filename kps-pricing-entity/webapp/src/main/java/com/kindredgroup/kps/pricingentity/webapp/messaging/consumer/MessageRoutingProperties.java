package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class represents a kps.config.message.routing section content from kps-config Helm Configmap.
 * Note, that the whole kps.config section from the Configmap is expected to be mapped to a YAML file where the configuration
 * properties can be extracted from. See spring.config.import section of the application-server.yaml file.
 *
 * @param routing maps a message type, e.g "Contest" to the {@link RoutingItem} object containing routing rules.
 */
@ConfigurationProperties(prefix = "message")
public record MessageRoutingProperties(Map<String, RoutingItem> routing) {
}
