update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.Entorn' where class = 'net.conselldemallorca.helium.model.hibernate.Entorn';
update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus' where class = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';

CREATE
    TABLE hel_map_sistra
    (
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


-- Canvi Versio --
ALTER TABLE hel_versio add column data_execucio TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE hel_versio add column proces_executat BOOLEAN;
update hel_idgen set valor = valor+1 where taula = 'hel_tasca';
update hel_versio set proces_executat = true, data_execucio = clock_timestamp()  where codi = 'inicial';
insert into hel_versio (id, codi, ordre, proces_executat, data_execucio) values ((select valor from hel_idgen where taula = 'hel_tasca' ),'2.1.0', 210, false, clock_timestamp());

