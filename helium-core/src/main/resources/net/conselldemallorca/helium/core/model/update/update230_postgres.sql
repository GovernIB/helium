-- Ordenació valors enumerats --
alter table hel_tasca add column tram_massiva boolean;
update hel_tasca set tram_massiva = false where tram_massiva is null;

-- Nous paràmetres pels documents
alter table hel_document add column convertir_ext character varying(10));
alter table hel_document add column extensions_permeses character varying(255);

-- Nous camps per autenticació en l'accés als WS
alter table hel_domini add column tipus_auth integer;
alter table hel_domini add column origen_creds integer;
alter table hel_domini add column usuari character varying(255);
alter table hel_domini add column contrasenya character varying(255);

-- Carrecs JBPM_ID depenents del grup
alter table hel_carrec_jbpmid add column grup character varying(64);

-- Nova taula hel_area_jbpmid
create table hel_area_jbpmid(
	id bigint not null,
	codi character varying(64) not null,
	nom character varying(255) not null,
	descripcio character varying(255),
	primary key (id));

-- Nous camps per a les accions
alter table hel_accio add column publica boolean not null default false;
alter table hel_accio add column oculta boolean not null default false;
alter table hel_accio add column cron character varying(255);

-- Nou camp per a optimitzar les consultes a domini
alter table hel_accio add column domini_cache_text boolean not null default false;

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
    '2.3.0' codi,
    230 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 230) = 0;
