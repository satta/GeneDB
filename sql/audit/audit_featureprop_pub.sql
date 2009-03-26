-- Autogenerated on Tue Mar 24 12:24:23 2009 by mkaudit.pl

create table audit.featureprop_pub (
    featureprop_pub_id integer not null
  , pub_id integer not null
  , featureprop_id integer not null
) inherits (audit.audit);

create or replace function audit.audit_featureprop_pub_insert_proc()
returns trigger
as $$
BEGIN
  raise exception 'Cannot insert directly into audit.featureprop_pub. Use one of the child tables.';
END;
$$ language plpgsql;
create trigger featureprop_pub_insert_tr before insert on audit.featureprop_pub
    for each statement execute procedure audit.audit_featureprop_pub_insert_proc();


create table audit.featureprop_pub_insert (
    constraint featureprop_pub_insert_ck check (type = 'INSERT')
) inherits (audit.featureprop_pub);
alter table audit.featureprop_pub_insert alter type set default 'INSERT';

create or replace function audit.public_featureprop_pub_insert_proc()
returns trigger
as $$
BEGIN
  insert into audit.featureprop_pub_insert (
      featureprop_pub_id, featureprop_id, pub_id
  ) values (
      new.featureprop_pub_id, new.featureprop_id, new.pub_id
  );
  return new;
END;
$$ language plpgsql;
create trigger featureprop_pub_audit_insert_tr after insert on public.featureprop_pub
    for each row execute procedure audit.public_featureprop_pub_insert_proc();


create table audit.featureprop_pub_update (
    constraint featureprop_pub_update_ck check (type = 'UPDATE')
  , old_featureprop_id integer not null
  , old_pub_id integer not null
) inherits (audit.featureprop_pub);
alter table audit.featureprop_pub_update alter type set default 'UPDATE';

create or replace function audit.public_featureprop_pub_update_proc()
returns trigger
as $$
BEGIN
  if old.featureprop_pub_id <> new.featureprop_pub_id or old.featureprop_pub_id is null <> new.featureprop_pub_id is null then
    raise exception 'If you want to change featureprop_pub.featureprop_pub_id (do you really?) then disable the audit trigger featureprop_pub_audit_update_tr';
  end if;
  insert into audit.featureprop_pub_update (
      featureprop_pub_id, featureprop_id, pub_id,
      old_featureprop_id, old_pub_id
   ) values (
       new.featureprop_pub_id, new.featureprop_id, new.pub_id,
       old.featureprop_id, old.pub_id
   );
  return new;
END;
$$ language plpgsql;
create trigger featureprop_pub_audit_update_tr after update on public.featureprop_pub
    for each row execute procedure audit.public_featureprop_pub_update_proc();


create table audit.featureprop_pub_delete (
    constraint featureprop_pub_delete_ck check (type = 'DELETE')
) inherits (audit.featureprop_pub);
alter table audit.featureprop_pub_delete alter type set default 'DELETE';

create or replace function audit.public_featureprop_pub_delete_proc()
returns trigger
as $$
BEGIN
  insert into audit.featureprop_pub_delete (
      featureprop_pub_id, featureprop_id, pub_id
  ) values (
      old.featureprop_pub_id, old.featureprop_id, old.pub_id
  );
  return old;
END;
$$ language plpgsql;
create trigger featureprop_pub_audit_delete_tr after delete on public.featureprop_pub
    for each row execute procedure audit.public_featureprop_pub_delete_proc();
