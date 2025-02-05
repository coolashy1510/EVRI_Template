create schema pricing_entity;

create table pricing_entity.contest
(
    id          bigint  not null generated always as identity (sequence name pricing_entity.contest_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    key         text,
    name        varchar not null,
    status      varchar not null,
    type        varchar not null,

    constraint contest_status_check check (status in ('Cancelled', 'Concluded', 'InPlay', 'PreGame')),
    constraint contest_type_check check (type in ('AlpineSkiing', 'Athletics', 'AussieRules', 'Badminton', 'Bandy', 'Baseball',
                                                  'Basketball', 'Basketball3x3', 'BeachSoccer', 'BeachVolleyball', 'Biathlon',
                                                  'Bobsleigh', 'Bowls', 'Boxing', 'Chess', 'Cricket', 'CrossCountry', 'Curling',
                                                  'Cycling', 'Darts', 'EBasketball', 'EIceHockey', 'ESoccer',
                                                  'ESportArenaOfValor', 'ESportCallOfDuty', 'ESportCounterStrike', 'ESportDota',
                                                  'ESportKingOfGlory', 'ESportLeagueOfLegends', 'ESportOverwatch',
                                                  'ESportRainbowSix', 'ESportRocketLeague', 'ESportStarCraft', 'ESportValorant',
                                                  'ESportWildRift', 'FieldHockey', 'FigureSkating', 'Floorball', 'Football',
                                                  'Formula1', 'FormulaE', 'FreestyleSkiing', 'Futsal', 'GaelicFootball',
                                                  'GaelicHurling', 'Golf', 'Greyhounds', 'Handball', 'Harness', 'Hearthstone',
                                                  'IceHockey', 'Kabaddi', 'Lacrosse', 'Luge', 'Mma', 'MotorcycleRacing',
                                                  'Netball', 'NordicCombined', 'Olympics', 'Padel', 'Pesapallo', 'Pool',
                                                  'RinkHockey', 'Rugby', 'Sailing', 'ShortTrack', 'Skeleton', 'SkiJumping',
                                                  'Snooker', 'Snowboard', 'SpeedSkating', 'Speedway', 'Squash', 'StockCarRacing',
                                                  'Surfing', 'Swimming', 'TableTennis', 'Tennis', 'Thoroughbred',
                                                  'VirtualGreyhounds', 'VirtualHarness', 'VirtualThoroughbred', 'Volleyball',
                                                  'Waterpolo')),
    constraint contest_pk primary key (id),
    constraint contest_key_unique unique (key)
);


create table pricing_entity.proposition
(
    id         bigint  not null generated always as identity (sequence name pricing_entity.proposition_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    key        varchar not null,
    contest_id bigint  not null references pricing_entity.contest (id) on delete cascade,
    name       varchar not null,
    type       varchar not null,
    constraint proposition_pk primary key (id),
    constraint proposition_unique unique (contest_id, key)
);

create table pricing_entity.option
(
    id                  bigint  not null generated always as identity (sequence name pricing_entity.option_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    proposition_id      bigint  not null references pricing_entity.proposition (id) on delete cascade,
    key                 varchar not null,
    name                varchar not null,
    type                varchar not null,
    constraint option_pk primary key (id),
    constraint option_unique unique (proposition_id, key),
    constraint option_type_check check (type in
                                        ('AnyOther', 'Draw', 'Even', 'No', 'NoGoal', 'None', 'Odd', 'Participant', 'Score', 'T1',
                                         'T1OrDraw', 'T1OrT2', 'T2', 'T2OrDraw', 'TotalExact', 'TotalOver', 'TotalScore',
                                         'TotalUnder', 'Unspecified', 'Yes'))
);

create table pricing_entity.variant
(
    id                bigint  not null generated always as identity (sequence name pricing_entity.variant_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    proposition_id    bigint  not null references pricing_entity.proposition (id) on delete cascade,
    key               varchar not null,
    name              varchar not null,
    type              varchar not null,
    constraint variant_pk primary key (id),
    constraint variant_unique unique (proposition_id, key),
    constraint variant_type_check check (type in ('Line', 'Margin', 'OverUnder', 'Plain', 'Tote'))
);

create table pricing_entity.price
(
    id             bigint not null generated always as identity (sequence name pricing_entity.price_id_seq minvalue 1 increment by 1 start with 1 no cycle),
    proposition_id bigint references pricing_entity.proposition (id) on delete cascade,
    option_id      bigint references pricing_entity.option (id) on delete no action,
    variant_id     bigint references pricing_entity.variant (id) on delete no action,
    price          numeric,

    constraint price_pk primary key (id),
    constraint price_unique unique (proposition_id, option_id, variant_id)
);

create table pricing_entity.outcome
(
    id             bigint not null, -- default sequence value is set below to generate inherited tables first
    proposition_id bigint references pricing_entity.proposition (id) on delete cascade,
    option_id      bigint references pricing_entity.option (id) on delete no action,
    variant_id     bigint references pricing_entity.variant (id) on delete no action,
    type           varchar,
    constraint outcome_pk primary key (id),
    constraint outcome_unique unique (proposition_id, option_id, variant_id)
);

create table pricing_entity.line_outcome
(
    like pricing_entity.outcome including all,
    handicap numeric(10, 2) not null,
    constraint line_outcome_type_check check (type like 'LineOutcome')
) inherits (pricing_entity.outcome);

create table pricing_entity.margin_outcome
(
    like pricing_entity.outcome including all,
    min_value numeric(10, 2) not null,
    max_value numeric(10, 2) not null,
    constraint margin_outcome_type_check check (type like 'MarginOutcome')
) inherits (pricing_entity.outcome);

create table pricing_entity.over_under_outcome
(
    like pricing_entity.outcome including all,
    total numeric(10, 2) not null,
    constraint over_under_outcome_type_check check (type like 'OverUnderOutcome')
) inherits (pricing_entity.outcome);

create table pricing_entity.plain_outcome
(
    like pricing_entity.outcome including all,
    constraint plain_outcome_type_check check (type like 'PlainOutcome')
) inherits (pricing_entity.outcome);

create table pricing_entity.tote_outcome
(
    like pricing_entity.outcome including all,
    constraint tote_outcome_type_check check (type like ' ToteOutcome')
) inherits (pricing_entity.outcome);

alter table pricing_entity.outcome
    alter column id add generated always as identity (sequence name pricing_entity.outcome_id_seq minvalue 1 increment by 1 start with 1 no cycle);
alter table pricing_entity.line_outcome
    alter column id add generated always as identity (sequence name pricing_entity.line_outcome_id_seq minvalue 1 increment by 1 start with 1 no cycle);
alter table pricing_entity.margin_outcome
    alter column id add generated always as identity (sequence name pricing_entity.margin_outcome_id_seq minvalue 1 increment by 1 start with 1 no cycle);
alter table pricing_entity.over_under_outcome
    alter column id add generated always as identity (sequence name pricing_entity.over_under_outcome_id_seq minvalue 1 increment by 1 start with 1 no cycle);
alter table pricing_entity.plain_outcome
    alter column id add generated always as identity (sequence name pricing_entity.plain_outcome_id_seq minvalue 1 increment by 1 start with 1 no cycle);
alter table pricing_entity.tote_outcome
    alter column id add generated always as identity (sequence name pricing_entity.tote_outcome_id_seq minvalue 1 increment by 1 start with 1 no cycle);
