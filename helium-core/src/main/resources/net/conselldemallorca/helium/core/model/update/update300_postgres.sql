ALTER TABLE hel_usuari_prefs ADD COLUMN cabecera_reducida bool;
ALTER TABLE hel_usuari_prefs ADD COLUMN listado INTEGER NULL;
ALTER TABLE hel_usuari_prefs ADD COLUMN consulta_id BIGINT;
ALTER TABLE hel_usuari_prefs ADD COLUMN filtro_tareas_activas bool;
ALTER TABLE hel_usuari_prefs ADD COLUMN num_elementos_pagina bool;

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
    '3.0.0' codi,
    300 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 300) = 0;
