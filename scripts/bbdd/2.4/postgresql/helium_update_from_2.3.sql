-- Retrocedir expedients
create table hel_expedient_log(
  id BIGINT NOT NULL,
  accio_tipus INTEGER NOT NULL,
  accio_params CHARACTER VARYING(255),
  data TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
  estat INTEGER NOT NULL,
  jbpm_logid BIGINT,
  process_instance_id BIGINT,
  target_id CHARACTER VARYING(255) NOT NULL,
  usuari CHARACTER VARYING(255) NOT NULL,
  expedient_id BIGINT NOT NULL,
  primary key (id),
  constraint hel_expedient_logs_fk foreign key (expedient_id) references public.hel_expedient (id));

-- Permisos per grup
alter table hel_expedient_tipus add column restringir_grup boolean default false;
alter table hel_expedient  add colum grup_codi character varying(64):

-- Tramitació massiva d'expedients
alter table hel_expedient_tipus add column tram_massiva boolean default false;

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
    '2.4.0' codi,
    240 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 240) = 0;
