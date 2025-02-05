DROP function IF EXISTS pricing_entity.archive_outdated_data();
CREATE PROCEDURE pricing_entity.archive_outdated_data()
    language 'plpgsql'
    AS '
    declare
        id_list bigint[];
        aux_contest_id bigint;
    begin
        select array_agg(c.id) into id_list from pricing_entity.contest c where (c.status = ''Concluded'' or c.status =''Cancelled'') and c.updated_at < now() - interval ''1 week'';
        IF array_length(id_list, 1) > 0 THEN
            FOREACH aux_contest_id IN ARRAY id_list
                LOOP
                    INSERT INTO pricing_entity.archived_contest (select * from pricing_entity.contest c where c.id = aux_contest_id);
                    insert into pricing_entity.archived_proposition (select * from pricing_entity.proposition p where p.contest_id = aux_contest_id);
                    insert into pricing_entity.archived_option (select * from pricing_entity.option o where o.proposition_id in (select p.id from pricing_entity.proposition p where p.contest_id = aux_contest_id));
                    insert into pricing_entity.archived_variant (select * from pricing_entity.variant v where v.proposition_id in (select p.id from pricing_entity.proposition p where p.contest_id = aux_contest_id));
                    insert into pricing_entity.archived_proposition_placeholder (select * from pricing_entity.proposition_placeholder pp where pp.proposition_id in (select p.id from pricing_entity.proposition p where p.contest_id = aux_contest_id));
                    insert into pricing_entity.archived_price (select * from pricing_entity.price pr where pr.proposition_id in (select p.id from pricing_entity.proposition p where p.contest_id = aux_contest_id));
                    delete FROM pricing_entity.contest c where c.id = aux_contest_id;
                END LOOP;
        END IF;
    END; '