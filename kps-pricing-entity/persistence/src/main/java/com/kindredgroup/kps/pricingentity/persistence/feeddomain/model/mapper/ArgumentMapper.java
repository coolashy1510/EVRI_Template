package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper;

import com.kindredgroup.kps.internal.api.pricingdomain.Argument;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.PropositionPlaceholder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArgumentMapper {

    ArgumentMapper INSTANCE = Mappers.getMapper(ArgumentMapper.class);

    default Argument propositionPlaceholderToArgument(PropositionPlaceholder propositionPlaceholder) {
        return new Argument(propositionPlaceholder.getName(), propositionPlaceholder.getValue());
    }

    default PropositionPlaceholder argumentToPropositionPlaceholder(Argument argument, Proposition proposition) {
        final PropositionPlaceholder result = new PropositionPlaceholder();
        result.setProposition(proposition);
        result.setName(argument.name());
        result.setValue(argument.value());
        return result;
    }

}

