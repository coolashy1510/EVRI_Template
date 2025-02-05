package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper;

import com.kindredgroup.kps.internal.api.pricingdomain.Entity;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.OptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EntityMapper {
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    @Mapping(target = "key", source = "key")
    @Mapping(target = "entityType", source = "type")
    Entity toEntity(OptionEntity entity);
}

