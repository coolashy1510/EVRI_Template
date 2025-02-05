create schema core_entity;
create sequence core_entity.area_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.area
(
    id       bigint  not null default nextval('core_entity.area_id_seq'),
    name     varchar not null,
    iso_code varchar not null,

    constraint area_pk primary key (id)

);

create sequence core_entity.competition_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.competition
(
    id           bigint  not null default nextval('core_entity.competition_id_seq'),
    key          text    not null,
    type         varchar not null,
    name         varchar not null,
    contest_type varchar not null,
    age_category varchar not null,
    gender       varchar not null,
    area_id      bigint  references core_entity.area (id) on delete set null,

    constraint competition_pk primary key (id),
    constraint competition_key_unique unique (key),
    constraint competition_type_check check (type in ('NotApplicable', 'Cup', 'League')),
    constraint competition_age_category_check check (age_category in
                                                     ('NotApplicable', 'Unconfirmed', 'Y10', 'U11', 'U12', 'U13', 'U14', 'U15',
                                                      'U16', 'U17', 'U18', 'U19', 'U20', 'U21', 'U23', 'U22', 'Juniors', 'Senior',
                                                      'Youth')),
    constraint competition_gender_check check (gender in ('NotKnown', 'Male', 'Female', 'Mixed'))

);

create sequence core_entity.tournament_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.tournament
(
    id             bigint                   not null default nextval('core_entity.tournament_id_seq'),
    key            text                     not null,
    name           varchar                  not null,
    starts_at      timestamp with time zone not null,
    ends_at        timestamp with time zone not null,
    competition_id bigint references core_entity.competition (id) on delete cascade,

    constraint tournament_pk primary key (id),
    constraint tournament_key_unique unique (key)

);

create sequence core_entity.venue_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.venue
(
    id           bigint  not null default nextval('core_entity.venue_id_seq'),
    key          text    not null,
    name         varchar not null,
    contest_type varchar not null,
    area_id      bigint  references core_entity.area (id) on delete set null,
    country_code varchar,

    constraint venue_pk primary key (id),
    constraint venue_key_unique unique (key)

);

create sequence core_entity.fixture_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.fixture
(
    id            bigint                   not null default nextval('core_entity.fixture_id_seq'),
    key           text                     not null,
    name          varchar                  not null,
    contest_type  varchar                  not null,
    starts_at     timestamp with time zone not null,
    group_name    varchar,
    round_type    varchar,
    tournament_id bigint references core_entity.tournament (id) on delete cascade,
    venue_id      bigint                   references core_entity.venue (id) on delete set null,

    constraint fixture_pk primary key (id),
    constraint fixture_key_unique unique (key),
    constraint fixture_round_type_check check (round_type in
                                               ('Unknown', 'Preliminary', 'PreliminaryFirstRound', 'PreliminarySecondRound',
                                                'PreliminaryThirdRound', 'PreliminarySemi', 'PreliminaryFinal', 'Group',
                                                'KnockOut', 'RoundOf128', 'RoundOf64', 'RoundOf32', 'RoundOf16', 'Quarter',
                                                'Semi', 'Final'))

);

create sequence core_entity.team_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.team
(
    id            bigint  not null default nextval('core_entity.team_id_seq'),
    key           text    not null,
    name          varchar not null,
    contest_type  varchar not null,
    area_id       bigint  references core_entity.area (id) on delete set null,
    home_venue_id bigint  references core_entity.venue (id) on delete set null,
    gender        varchar not null,

    constraint team_pk primary key (id),
    constraint team_key_unique unique (key),
    constraint team_gender_check check (gender in ('NotKnown', 'Male', 'Female', 'Mixed'))

);

create table core_entity.team_fixture
(
    team_id    bigint references core_entity.team (id) on update cascade on delete cascade,
    fixture_id bigint references core_entity.fixture (id) on update cascade on delete cascade,

    constraint team_fixture_pk primary key (team_id, fixture_id)
);

create sequence core_entity.participant_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.participant
(
    id            bigint                   not null default nextval('core_entity.participant_id_seq'),
    key           text                     not null,
    name          varchar                  not null,
    contest_type  varchar                  not null,
    gender        varchar                  not null,
    date_of_birth timestamp with time zone not null,
    area_id       bigint                   references core_entity.area (id) on delete set null,

    constraint participant_pk primary key (id),
    constraint participant_key_unique unique (key),
    constraint participant_gender_check check (gender in ('NotKnown', 'Male', 'Female', 'Mixed'))

);


create table core_entity.team_participant
(
    team_id        bigint references core_entity.team (id) on update cascade on delete cascade,
    participant_id bigint references core_entity.participant (id) on update cascade on delete cascade,
    tournament_id  bigint references core_entity.tournament (id) on update cascade on delete cascade,

    constraint team_participant_pk primary key (team_id, participant_id, tournament_id)
);

create sequence core_entity.lineup_id_seq minvalue 1 increment by 1 start with 1 no cycle;
create table core_entity.lineup
(
    id             bigint not null default nextval('core_entity.lineup_id_seq'),
    fixture_id     bigint not null references core_entity.fixture (id) on update cascade on delete cascade,
    team_id        bigint references core_entity.team (id) on update cascade on delete cascade,
    participant_id bigint not null references core_entity.participant (id) on update cascade on delete cascade,

    constraint lineup_pk primary key (id),
    constraint lineup_unique unique (fixture_id, team_id, participant_id)
);
