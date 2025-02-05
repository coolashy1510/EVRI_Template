create index if not exists archived_price_proposition_id_index on pricing_entity.archived_price using hash (proposition_id);
create index if not exists archived_outcome_proposition_id_index on pricing_entity.archived_outcome using hash (proposition_id);
create index if not exists archived_option_proposition_id_index on pricing_entity.archived_option using hash (proposition_id);
create index if not exists archived_variant_proposition_id_index on pricing_entity.archived_variant using hash (proposition_id);
create index if not exists archived_proposition_placeholder_proposition_id_index on pricing_entity.archived_proposition_placeholder using hash (proposition_id);
create index if not exists archived_proposition_contest_id_index on pricing_entity.archived_proposition using hash (contest_id);
