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
update hel_versio set proces_executat = true, data_execucio = clock_timestamp()  where codi = 'inicial';
insert into hel_versio (id, codi, ordre, proces_executat, data_execucio) values ((select valor from hel_idgen where taula = 'hel_tasca' ),'2.1.1', 211, false, clock_timestamp());

