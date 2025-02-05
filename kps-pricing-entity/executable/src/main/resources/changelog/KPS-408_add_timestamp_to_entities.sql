ALTER TABLE pricing_entity.contest
    add column if not exists updated_at timestamptz default CURRENT_TIMESTAMP not null;
ALTER TABLE pricing_entity.option
    add column if not exists updated_at timestamptz default CURRENT_TIMESTAMP not null;
ALTER TABLE pricing_entity.price
    add column if not exists updated_at timestamptz default CURRENT_TIMESTAMP not null;
ALTER TABLE pricing_entity.proposition
    add column if not exists updated_at timestamptz default CURRENT_TIMESTAMP not null;
ALTER TABLE pricing_entity.variant
    add column if not exists updated_at timestamptz default CURRENT_TIMESTAMP not null;

CREATE
OR REPLACE FUNCTION pricing_entity.update_timestamp_task()
RETURNS TRIGGER AS '
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END; '
LANGUAGE 'plpgsql';


CREATE TRIGGER update_timestamp_trigger_contest
    BEFORE UPDATE
    ON
        pricing_entity.contest
    FOR EACH ROW
    EXECUTE PROCEDURE pricing_entity.update_timestamp_task();

CREATE TRIGGER update_timestamp_trigger_option
    BEFORE UPDATE
    ON
        pricing_entity.option
    FOR EACH ROW
    EXECUTE PROCEDURE pricing_entity.update_timestamp_task();

CREATE TRIGGER update_timestamp_trigger_price
    BEFORE UPDATE
    ON
        pricing_entity.price
    FOR EACH ROW
    EXECUTE PROCEDURE pricing_entity.update_timestamp_task();

CREATE TRIGGER update_timestamp_trigger_proposition
    BEFORE UPDATE
    ON
        pricing_entity.proposition
    FOR EACH ROW
    EXECUTE PROCEDURE pricing_entity.update_timestamp_task();

CREATE TRIGGER update_timestamp_trigger_variant
    BEFORE UPDATE
    ON
        pricing_entity.variant
    FOR EACH ROW
    EXECUTE PROCEDURE pricing_entity.update_timestamp_task();
