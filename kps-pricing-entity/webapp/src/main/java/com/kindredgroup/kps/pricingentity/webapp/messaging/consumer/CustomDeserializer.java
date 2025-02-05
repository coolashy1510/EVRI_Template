package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.jackson.KpsObjectMapper;
import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.internal.kafka.MessageType;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
public class CustomDeserializer extends JsonDeserializer<Object> {

    private static final String V1 = "1";
    private static final String V2 = "2";
    private final KpsObjectMapper kpsObjectMapper = new KpsObjectMapper();
    private final Map<String, Map<String, Class<?>>> messageToVersionTypesMap;

    public CustomDeserializer() {
        this.messageToVersionTypesMap = Map.of(
                MessageType.Contest.name(), Map.of(V1, Contest.class),
                MessageType.Proposition.name(), Map.of(V1, Proposition.class, V2, PropositionV2.class),
                MessageType.OptionChanged.name(), Map.of(V1, OptionChanged.class),
                MessageType.VariantChanged.name(), Map.of(V1, VariantChanged.class),
                MessageType.OutcomeResult.name(), Map.of(V1, OutcomeResult.class),
                MessageType.PriceChangedCollection.name(), Map.of(V1, PriceChangedCollection.class),
                MessageType.PropositionChanged.name(), Map.of(V1, PropositionChanged.class)
        );
    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
        if (data == null) {
            return null;
        }
        FeedDomainMetadata metadata = FeedDomainMetadata.from(headers);
        try {
            final var valueClass = Optional.ofNullable(messageToVersionTypesMap.get(metadata.messageType()))
                                           .map(versionToTypeMap -> versionToTypeMap.get(
                                                   metadata.majorVersion()));
            if (valueClass.isPresent()) {
                return kpsObjectMapper.readValue(data, valueClass.get());
            }
            log.debug("Error trying to parse a message, messageType {} is not supported", metadata.messageType());
        } catch (IOException | IllegalArgumentException e) {
            log.debug("Error de-serialising message {}. Exception {} thrown", metadata.messageType(), e.getMessage());
        }
        return null;
    }
}
