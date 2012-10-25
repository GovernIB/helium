ALTER TABLE helium.public.jbpm_variableinstance ADD COLUMN aux_clob TEXT;
UPDATE helium.public.jbpm_variableinstance SET aux_clob = stringvalue_;
ALTER TABLE helium.public.jbpm_variableinstance DROP COLUMN stringvalue_;
ALTER TABLE helium.public.jbpm_variableinstance RENAME COLUMN aux_clob TO stringvalue_;	


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
    '2.5.0' codi,
    250 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 250) = 0;

--select de consultes per tipus
ALTER TABLE hel_camp ADD COLUMN consulta_camp_text CHARACTER VARYING(64);
ALTER TABLE hel_camp ADD COLUMN consulta_camp_valor CHARACTER VARYING(64);
ALTER TABLE hel_camp ADD COLUMN consulta_params CHARACTER VARYING(255);