drop trigger if exists update_timestamp_trigger_contest on pricing_entity.contest;
drop trigger if exists update_timestamp_trigger_price on pricing_entity.price;
drop trigger if exists update_timestamp_trigger_proposition on pricing_entity.proposition;
drop trigger if exists update_timestamp_trigger_variant on pricing_entity.variant;
drop trigger if exists update_timestamp_trigger_option on pricing_entity.option;

drop function if exists pricing_entity.update_timestamp_task();