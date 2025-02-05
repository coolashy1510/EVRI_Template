create table pricing_entity.proposition_placeholder
(
    id             bigint not null generated always as identity (sequence name pricing_entity.proposition_placeholder_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    name           text   not null,
    value          text   not null,
    proposition_id bigint not null references pricing_entity.proposition (id) on delete cascade,

    constraint proposition_placeholder_pk primary key (id)
);
