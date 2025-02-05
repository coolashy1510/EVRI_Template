package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.text.MessageFormat;
import java.util.List;

import com.kindredgroup.kps.internal.api.EnumValueSupplier;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Generic class to convert Java enums to/from DB string field.
 * <p/>
 * Should be extended with a class parametrized with an enum implementing {@link EnumValueSupplier} interface.
 * <p/>
 * The concrete class can be used in an {@link jakarta.persistence.Entity} class in the {@link Converter} annotation.
 */
@Converter
public abstract class EnumConverter<T extends EnumValueSupplier> implements AttributeConverter<T, String> {

    @Override
    public String convertToDatabaseColumn(T type) {
        if (type == null) {
            return null;
        }
        return type.getValue();
    }

    protected abstract List<T> getValues();

    @Override
    public T convertToEntityAttribute(String type) {
        if (type == null) {
            return null;
        }
        return getValues().stream().filter(c -> c.getValue().equals(type)).findFirst()
                          .orElseThrow(() -> new IllegalArgumentException(
                                  MessageFormat.format("Could not convert {0} by {1}", type, this.getClass().getSimpleName())));
    }
}
