create table userservice.user
(
    id         uuid primary key default gen_random_uuid(),
    name       varchar(30),
    surname    varchar(30),
    birth_date date,
    email      varchar(100) unique,
    active     boolean,
    created_at timestamp        default now(),
    updated_at timestamp        default now()
);

create index idx_email on userservice.user (email);
create index idx_active_user on userservice.user (active);
create index idx_name_surname on userservice.user (name, surname);
