DROP TRIGGER IF EXISTS update_timestamp_trigger_contest
    ON pricing_entity.contest;

DROP TRIGGER IF EXISTS update_timestamp_trigger_option
    ON pricing_entity.option;

DROP TRIGGER IF EXISTS update_timestamp_trigger_price
    ON pricing_entity.price;

DROP TRIGGER IF EXISTS update_timestamp_trigger_proposition
    ON pricing_entity.proposition;

DROP TRIGGER IF EXISTS update_timestamp_trigger_variant
    ON pricing_entity.variant;

DROP TABLE IF EXISTS pricing_entity.archived_contest;
DROP TABLE IF EXISTS pricing_entity.archived_option;
DROP TABLE IF EXISTS pricing_entity.archived_price;
DROP TABLE IF EXISTS pricing_entity.archived_proposition;
DROP TABLE IF EXISTS pricing_entity.archived_proposition_placeholder;
DROP TABLE IF EXISTS pricing_entity.archived_variant;