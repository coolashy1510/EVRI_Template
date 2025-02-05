create index if not exists proposition_contest_id_index on pricing_entity.proposition using hash(contest_id);

create index if not exists variant_proposition_id_index on pricing_entity.variant using hash(proposition_id);

create index if not exists option_proposition_id_index on pricing_entity.option using hash(proposition_id);

create index if not exists price_proposition_id_index on pricing_entity.price using hash(proposition_id);
create index if not exists price_option_id_index on pricing_entity.price using hash(option_id);
create index if not exists price_variant_id_index on pricing_entity.price using hash(variant_id);

create index if not exists proposition_placeholder_proposition_id_index on pricing_entity.proposition_placeholder using hash(proposition_id);

create index if not exists outcome_proposition_id_index on pricing_entity.outcome using hash(proposition_id);
create index if not exists outcome_option_id_index on pricing_entity.outcome using hash(option_id);
create index if not exists outcome_variant_id_index on pricing_entity.outcome using hash(variant_id);


create index if not exists competition_area_id_index on core_entity.competition using hash(area_id);

create index if not exists fixture_venue_id_index on core_entity.fixture using hash(venue_id);
create index if not exists fixture_tournament_id_index on core_entity.fixture using hash(tournament_id);

create index if not exists lineup_fixture_id_index on core_entity.lineup using hash(fixture_id);
create index if not exists lineup_participant_id_index on core_entity.lineup using hash(participant_id);
create index if not exists lineup_team_id_index on core_entity.lineup using hash(team_id);

create index if not exists participant_area_id_index on core_entity.participant using hash(area_id);

create index if not exists team_area_id_index on core_entity.team using hash(area_id);
create index if not exists team_home_venue_id_index on core_entity.team using hash(home_venue_id);

create index if not exists team_fixture_team_id_index on core_entity.team_fixture using hash(team_id);
create index if not exists team_fixture_fixture_id_index on core_entity.team_fixture using hash(fixture_id);

create index if not exists team_participant_tournament_id_index on core_entity.team_participant using hash(tournament_id);
create index if not exists team_participant_participant_id_index on core_entity.team_participant using hash(participant_id);
create index if not exists team_participant_team_id_index on core_entity.team_participant using hash(team_id);

create index if not exists tournament_competition_id_index on core_entity.tournament using hash(competition_id);

create index if not exists venue_area_id_index on core_entity.venue using hash(area_id);

