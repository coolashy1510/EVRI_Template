package com.kindredgroup.kps.pricingentity.logging;

import com.kindredgroup.commons.logging.common.CustomField;
import com.kindredgroup.commons.logging.common.CustomFieldType;


public enum KafkaLoggingMarker implements CustomField {

    PARTITION_ID("partitionId", CustomFieldType.MARKER),
    MEMBER_ID("memberId", CustomFieldType.MARKER);

    private final String name;
    private final CustomFieldType type;

    KafkaLoggingMarker(final String name, CustomFieldType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CustomFieldType getType() {
        return type;
    }


}
