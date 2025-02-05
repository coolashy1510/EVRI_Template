create table pricing_entity.option_entity
(
    id        bigint not null generated always as identity (sequence name pricing_entity.option_entity_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    key       text   not null,
    type      text   not null,
    option_id bigint not null references pricing_entity.option (id) on delete cascade,

    constraint option_entity_pk primary key (id),
    constraint option_entity_unique unique (option_id, key)
);

create index if not exists proposition_placeholder_proposition_id_index on pricing_entity.option_entity using hash (option_id);
