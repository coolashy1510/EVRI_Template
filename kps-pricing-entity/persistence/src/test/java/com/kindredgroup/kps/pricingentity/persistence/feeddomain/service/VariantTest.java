package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class VariantTest {

    @Test
    void create_nullType_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Variant("key1", mock(Proposition.class), null));
    }

    @Test
    void create_nullProposition_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Variant("key1", null, mock(VariantType.class)));
    }

}
