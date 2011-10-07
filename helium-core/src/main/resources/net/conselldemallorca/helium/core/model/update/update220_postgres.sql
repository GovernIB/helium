-- Consultes avançades --
alter table hel_consulta add column informe_contingut oid;
alter table hel_consulta add column exportar_actiu BOOLEAN;
alter table hel_consulta alter column exportar_actiu set default true;
update hel_consulta set exportar_actiu = true;

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
    '2.2.0' codi,
    220 ordre,
    clock_timestamp() data_creacio,
    0 proces_executat,
    1 script_executat,
    clock_timestamp() data_execucio_script
from dual
where (select count(*) from hel_versio where ordre = 220) = 0;
