drop index pricing_entity.proposition_placeholder_proposition_id_index;
create index if not exists option_entity_option_id_index on pricing_entity.option_entity using hash (option_id);
