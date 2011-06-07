-- Canvi a MAVEN --
update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.Entorn' where class = 'net.conselldemallorca.helium.model.hibernate.Entorn';
update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus' where class = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';

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

CREATE
    TABLE hel_enumeracio_valors
    (
        id BIGINT NOT NULL,
        codi CHARACTER VARYING(64) NOT NULL,
        nom CHARACTER VARYING(255) NOT NULL,
        enumeracio_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT hel_enumeracio_valors_fk FOREIGN KEY (enumeracio_id) REFERENCES
        public.hel_enumeracio (id)
    );

    
-- Gestió de versions (versió inicial)--
alter table hel_versio add column proces_executat BOOLEAN;
alter table hel_versio add column data_execucio_proces TIMESTAMP(6) WITHOUT TIME ZONE;
alter table hel_versio add column script_executat BOOLEAN;
alter table hel_versio add column data_execucio_script TIMESTAMP(6) WITHOUT TIME ZONE;
alter table hel_versio add column errorversio CHARACTER VARYING(255);
update hel_versio set proces_executat = true, data_execucio_proces = clock_timestamp(), script_executat = true, data_execucio_script = clock_timestamp()  where codi = 'inicial';

-- Annexió automática de documents generats amb plantilla --
alter table hel_document add column adjuntar_auto BOOLEAN;
update hel_document set adjuntar_auto = true;

-- Actualització a la nova versió --
update hel_idgen set valor = valor+1 where taula = 'hel_versio';
insert into hel_versio (id, codi, ordre, script_executat, data_execucio_script, proces_executat) values ((select valor from hel_idgen where taula = 'hel_versio' ),'2.1.0', 210, true, clock_timestamp(), false);
update hel_versio set script_executat = true, data_execucio_script = clock_timestamp() where codi = '2.1.0';
