alter table users
    add username varchar(20) not null unique default 'GUEST-' || substring(gen_random_uuid()::text, 1, 8);