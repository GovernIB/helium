update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.Entorn' where class = 'net.conselldemallorca.helium.model.hibernate.Entorn';
update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus' where class = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';

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
    )


-- Canvi Versio --
update hel_idgen set valor = valor+1 where taula = 'hel_tasca';
insert into hel_versio (id, codi, ordre, script_executat, data_execucio_script, proces_executat) values ((select valor from hel_idgen where taula = 'hel_tasca' ),'2.1.1', 211, true, clock_timestamp(), false);
update hel_versio set script_executat = true, data_execucio_script = clock_timestamp() where codi = '2.1.1';

