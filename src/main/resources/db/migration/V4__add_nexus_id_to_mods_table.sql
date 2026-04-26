alter table mods
    add nexus_id int,
    add constraint ck_mods_nexus_id_positive check (nexus_id > 0);