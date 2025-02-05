create index if not exists archived_contest_updated_at_index on pricing_entity.archived_contest using hash (updated_at);
