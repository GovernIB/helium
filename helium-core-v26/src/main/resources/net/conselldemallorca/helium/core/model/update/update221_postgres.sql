-- Ordenació valors enumerats --
alter table hel_enumeracio_valors add column ordre int;
update hel_enumeracio_valors set ordre = 0;

-- Actualització a la nova versió --
insert into hel_versio (
    id,
    codi,
    ordre,
    data_creacio,
    proces_executat,
    script_executat,
    data_execucio_script)
select
    nextval('hibernate_sequence') id,
    '2.2.1' codi,
    221 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 221) = 0;
