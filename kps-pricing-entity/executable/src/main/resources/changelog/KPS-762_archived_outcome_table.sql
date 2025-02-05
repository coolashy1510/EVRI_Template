CREATE TABLE pricing_entity.archived_outcome as table pricing_entity.outcome with no data;
ALTER TABLE pricing_entity.archived_outcome add constraint archived_outcome_pk primary key (id);

create index outcome_proposition_id_index on pricing_entity.outcome using hash(proposition_id);
create index outcome_option_id_index on pricing_entity.outcome using hash(option_id);
create index outcome_variant_id_index on pricing_entity.outcome using hash(variant_id);