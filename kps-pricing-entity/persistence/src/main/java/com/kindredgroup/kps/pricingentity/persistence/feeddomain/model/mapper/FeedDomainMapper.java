package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import org.jetbrains.annotations.NotNull;

public class FeedDomainMapper {

    /*TODO:nikita.shvinagir:2023-07-11: this class contains potential flaws in all static methods
    The main root cause of the flaws is the fact that for each feed domain class T the corresponding persistence class V does
    not contains a number of fields.
    As a result, after the mapping the T object does not have some fields that we use when producing/consuming Kafka messages,
    for example startDateTimeUtc, groupingId, etc.
    The status for the static method from this class is the following:
        getContest - the result is never used
        getOption, getVariant - implicitly used in the proposition save and update. See below.
        getProposition - the result is never used
    At the moment this code is useless.
    Also, if at some point we decide to use the persistence result to compose the Kafka messages we publish to pricingdomain
    (at the moment we just use the inbound payloads), using this code might introduce bugs and anyways will result into
    reworking this code.

    Preliminary options are:
    - remove this class and make all the service methods return void
    - remove this class and make all the service methods return the persistence V classes instead
    - an option #42
     */

    public static com.kindredgroup.kps.internal.api.pricingdomain.Contest getContest(@NotNull final Contest contest) {
        return com.kindredgroup.kps.internal.api.pricingdomain.Contest.builder()
                                                                      .contestKey(contest.getKey())
                                                                      .contestType(contest.getType().getValue())
                                                                      .name(contest.getName())
                                                                      .status(contest.getStatus())
                                                                      .build();
    }

}
