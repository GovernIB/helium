-- Tipus de dada dels camps tipus textarea
alter table jbpm_variableinstance add column aux_clob text;
update jbpm_variableinstance set aux_clob = stringvalue_;
alter table jbpm_variableinstance drop column stringvalue_;
alter table jbpm_variableinstance rename column aux_clob TO stringvalue_;	

-- Select de consultes per tipus
alter table hel_camp add column consulta_camp_text character varying(64);
alter table hel_camp add column consulta_camp_valor character varying(64);
alter table hel_camp add column consulta_params character varying(255);

-- Millora missatges errors integracions
alter table hel_portasignatures add process_instance_id character varying(255);
alter table hel_portasignatures add expedient_id number(19);
alter table hel_portasignatures add constraint hel_expedient_psigna_fk foreign key (expedient_id) references public.hel_expedient (id);
alter table hel_expedient add errors_integs boolean not null set default 0;

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
