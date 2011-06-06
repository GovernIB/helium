update HEL_ACL_CLASS set CLASS = 'net.conselldemallorca.helium.core.model.hibernate.Entorn' where CLASS = 'net.conselldemallorca.helium.model.hibernate.Entorn';
update HEL_ACL_CLASS set CLASS = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus' where CLASS = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';

CREATE
    TABLE hel_enumeracio_valors
    (
        id NUMBER(19) NOT NULL,
        codi VARCHAR2(64 CHAR) NOT NULL,
        nom VARCHAR2(255 CHAR) NOT NULL,
        enumeracio_id NUMBER(19) NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT hel_enumeracio_valors_fk FOREIGN KEY (enumeracio_id) REFERENCES
        public.hel_enumeracio (id)
    )

-- Canvi Versio --
update HEL_IDGEN set VALOR = VALOR+1 where TAULA = 'hel_versio';
update HEL_VERSIO set PROCES_EXECUTAT = 1, DATA_EXECUCIO = SYSDATE where CODI = 'inicial';
insert into HEL_VERSIO (ID, CODI, ORDRE, PROCES_EXECUTAT, DATA_EXECUCIO) values ((select VALOR from HEL_IDGEN where TAULA = 'hel_versio' ),'2.1.1', 211, 0, SYSDATE);
