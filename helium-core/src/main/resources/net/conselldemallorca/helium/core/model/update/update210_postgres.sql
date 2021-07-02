-- Canvi a MAVEN --
update hel_acl_class set class = 'es.caib.helium.persist.entity.Entorn' where class = 'net.conselldemallorca.helium.model.hibernate.Entorn';
update hel_acl_class set class = 'es.caib.helium.persist.entity.ExpedientTipus' where class = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';

-- Mapejos de SISTRA --
create table hel_map_sistra(
        id BIGINT NOT NULL,
        codihelium CHARACTER VARYING(255) NOT NULL,
        codisistra CHARACTER VARYING(255) NOT NULL,
        tipus INTEGER NOT NULL,
        expedient_tipus_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT hel_exptipus_map_sistra_fk FOREIGN KEY (expedient_tipus_id) REFERENCES
        public.hel_expedient_tipus (id),
        UNIQUE (codihelium, expedient_tipus_id)
    );
    
-- Gestió de versions (versió inicial)--
alter table hel_versio add column data_creacio TIMESTAMP(6) WITHOUT TIME ZONE;
alter table hel_versio add column proces_executat BOOLEAN;
alter table hel_versio alter column proces_executat SET DEFAULT FALSE;
alter table hel_versio add column data_execucio_proces TIMESTAMP(6) WITHOUT TIME ZONE;
alter table hel_versio add column script_executat BOOLEAN;
alter table hel_versio alter column script_executat SET DEFAULT FALSE;
alter table hel_versio add column data_execucio_script TIMESTAMP(6) WITHOUT TIME ZONE;
alter table hel_versio add column errorversio CHARACTER VARYING(255);
update hel_versio set proces_executat = true, data_execucio_proces = clock_timestamp(), script_executat = true, data_execucio_script = clock_timestamp()  where codi = 'inicial';

-- Annexió automática de documents generats amb plantilla --
alter table hel_document add column adjuntar_auto BOOLEAN;
alter table hel_document alter column adjuntar_auto SET DEFAULT FALSE;
update hel_document set adjuntar_auto = true;

-- Manteniment valors enumeracions --
create table hel_enumeracio_valors(
        id BIGINT NOT NULL,
        codi CHARACTER VARYING(64) NOT NULL,
        nom CHARACTER VARYING(255) NOT NULL,
        enumeracio_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT hel_enumeracio_valors_fk FOREIGN KEY (enumeracio_id) REFERENCES
        public.hel_enumeracio (id)
    );

-- Camps per definir si s'ha de generar una alerta quan se completa una tasca amb un termini associat --
alter table hel_termini add column alerta_completat BOOLEAN;
alter table hel_termini alter column alerta_completat SET DEFAULT FALSE;
update hel_termini set alerta_completat = false;

alter table hel_termini_iniciat add column data_completat DATE;
alter table hel_termini_iniciat add column alerta_completat BOOLEAN;
alter table hel_termini_iniciat alter column alerta_completat SET DEFAULT FALSE;
update hel_termini_iniciat set alerta_completat = false;

-- Prioritat i causa de les alertes --
alter table hel_alerta add column prioritat INTEGER;
alter table hel_alerta add column causa CHARACTER VARYING(255);

-- Enumeracions a nivell de tipus d'expedient --
alter table hel_enumeracio add column expedient_tipus_id BIGINT;
alter table hel_enumeracio add CONSTRAINT hel_exptip_enumeracio_fk FOREIGN KEY (expedient_tipus_id) REFERENCES
        public.hel_expedient_tipus (id);

-- Anulació d'expedients --
alter table hel_expedient add column anulat BOOLEAN;
alter table hel_expedient alter column anulat SET DEFAULT FALSE;
update hel_expedient set anulat = false;

-- Idioma en les preferencies d'usuari --
alter table hel_usuari_prefs add column idioma CHARACTER VARYING(255);

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
    '2.1.0' codi,
    210 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 210) = 0;
