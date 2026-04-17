create table users(
    id uuid primary key default gen_random_uuid(),
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(20) not null default 'GUEST',
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    deleted_at timestamptz null
);