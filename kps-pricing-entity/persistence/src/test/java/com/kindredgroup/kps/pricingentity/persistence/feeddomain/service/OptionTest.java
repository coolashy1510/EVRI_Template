package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OptionTest {

    @Test
    void create_nullType_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Option("key1", mock(Proposition.class), null));
    }

    @Test
    void create_nullProposition_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Option("key1", null, mock(OptionType.class)));
    }

}
