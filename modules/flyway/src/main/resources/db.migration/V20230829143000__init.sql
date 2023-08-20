CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users
(
    id              uuid primary key unique default uuid_generate_v4(),
    phone_number    varchar(15) not null unique ,
    name            varchar(30) not null,
    password        varchar     not null,
    activate_date   timestamp,
    block_date      timestamp,
    last_login_date timestamp,
    created_at      timestamp   not null    default current_timestamp
);

CREATE TABLE IF NOT EXISTS user_access_token
(
    id              uuid primary key unique default uuid_generate_v4(),
    access_token    varchar   not null,
    refresh_token   varchar   not null,
    expiration_date timestamp not null,
    user_id         uuid      not null,
    constraint user_id_on_user_token foreign key (user_id) references users(id) match full on delete cascade on update cascade
);

create index if not exists ndx_user_id_on_user_token on user_access_token(user_id);

CREATE TABLE IF NOT EXISTS bank_access_token
(
    id              uuid primary key unique default uuid_generate_v4(),
    access_token    varchar   not null,
    refresh_token   varchar   not null,
    expiration_date timestamp not null,
    user_id         uuid      not null,
    constraint user_id_on_bank_token foreign key (user_id) references users(id) match full on delete cascade on update cascade
);

create index if not exists ndx_user_id_on_bank_token on bank_access_token(user_id);

