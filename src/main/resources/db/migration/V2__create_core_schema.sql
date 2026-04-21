create table modlists (
    id uuid primary key default gen_random_uuid(),
    name varchar(255) not null,
    description varchar(5000),
    is_public boolean not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    user_id uuid not null,
    constraint fk_modlists_users foreign key (user_id) references users(id)
);

CREATE INDEX idx_modlists_user_id ON modlists(user_id);

create table categories(
    id uuid primary key default gen_random_uuid(),
    nexus_id int not null unique,
    name varchar(255) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint ck_categories_nexus_id_positive check (nexus_id > 0)
);

create table mods(
    id uuid primary key default gen_random_uuid(),
    name varchar(255) not null,
    notes varchar(500) null,
    priority int not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    modlist_id uuid not null,
    category_id uuid null,
    constraint uq_mods_modlist_id_priority unique (modlist_id, priority),
    constraint ck_mods_priority_positive_or_zero check (priority >= 0),
    constraint fk_mods_modlists foreign key (modlist_id) references modlists(id) on delete cascade,
    constraint fk_mods_categories foreign key (category_id) references categories(id) on delete set null
);

CREATE INDEX idx_mods_modlist_id ON mods(modlist_id);
CREATE INDEX idx_mods_category_id ON mods(category_id);

create table plugins (
    id uuid primary key default gen_random_uuid(),
    name varchar(255) not null,
    priority int not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    modlist_id uuid not null,
    mod_id uuid null,
    constraint uq_plugins_modlist_id_priority unique (modlist_id, priority),
    constraint ck_plugins_priority_positive_or_zero check (priority >= 0),
    constraint fk_plugins_modlists foreign key (modlist_id) references modlists(id) on delete cascade,
    constraint fk_plugins_mods foreign key (mod_id) references mods(id) on delete cascade
);

CREATE INDEX idx_plugins_modlist_id ON plugins(modlist_id);
CREATE INDEX idx_plugins_mod_id ON plugins(mod_id);


