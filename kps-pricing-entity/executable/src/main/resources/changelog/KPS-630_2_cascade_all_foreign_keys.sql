alter table pricing_entity.price
    drop constraint price_variant_id_fkey,
    drop constraint price_option_id_fkey,
    add constraint price_variant_id_fkey foreign key (variant_id) references pricing_entity.variant (id) on delete cascade,
    add constraint price_option_id_fkey foreign key (option_id) references pricing_entity.option (id) on delete cascade;

alter table pricing_entity.outcome
drop constraint outcome_variant_id_fkey,
    drop constraint outcome_option_id_fkey,
    add constraint outcome_variant_id_fkey foreign key (variant_id) references pricing_entity.variant (id) on delete cascade,
    add constraint outcome_option_id_fkey foreign key (option_id) references pricing_entity.option (id) on delete cascade;
