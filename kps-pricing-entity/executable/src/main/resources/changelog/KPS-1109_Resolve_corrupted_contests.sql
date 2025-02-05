delete from pricing_entity.contest c where c.type = 'ESoccer';
alter table pricing_entity.contest drop constraint contest_status_check;
alter table pricing_entity.contest add constraint contest_status_check check (status in ('Cancelled', 'Concluded', 'InPlay', 'PreGame', 'Suspended'));
