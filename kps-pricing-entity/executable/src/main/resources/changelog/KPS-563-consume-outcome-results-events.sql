drop sequence if exists outcome_id_seq;
drop table if exists pricing_entity.outcome CASCADE;

create table pricing_entity.outcome
(
    id                 bigint not null generated always as identity (sequence name pricing_entity.outcome_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    proposition_id     bigint references pricing_entity.proposition (id) on delete cascade,
    option_id          bigint references pricing_entity.option (id) on delete no action,
    variant_id         bigint references pricing_entity.variant (id) on delete no action,
    refund_numerator   integer,
    refund_denominator integer,
    win_numerator      integer,
    win_denominator    integer,
    constraint outcome_pk primary key (id),
    constraint outcome_unique unique (proposition_id, option_id, variant_id)
);


