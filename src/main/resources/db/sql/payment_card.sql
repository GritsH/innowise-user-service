create table userservice.payment_card
(
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    number          varchar(16) unique,
    holder          varchar(100),
    expiration_date date,
    active          boolean,
    created_at      timestamp default now(),
    updated_at      timestamp default now(),
    foreign key (user_id) references userservice.user (id)
);

create index idx_holder on userservice.payment_card (holder);
create index idx_active_card on userservice.payment_card (active);
create index idx_expiration_date on userservice.payment_card (expiration_date);