update HEL_ACL_CLASS set CLASS = 'net.conselldemallorca.helium.core.model.hibernate.Entorn' where CLASS = 'net.conselldemallorca.helium.model.hibernate.Entorn';
update HEL_ACL_CLASS set CLASS = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus' where CLASS = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';

CREATE
    TABLE HEL_MAP_SISTRA
    (
        ID NUMBER(19) NOT NULL,
        CODIHELIUM VARCHAR2(255 CHAR) NOT NULL,
        CODISISTRA VARCHAR2(255 CHAR) NOT NULL,
        TIPUS NUMBER(10) NOT NULL,
        EXPEDIENT_TIPUS_ID NUMBER(19) NOT NULL,
        PRIMARY KEY (ID),
        CONSTRAINT HEL_EXPTIPUS_MAP_SISTRA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES
        HEL_EXPEDIENT_TIPUS (ID),
        CONSTRAINT SYS_C0035059 UNIQUE (CODIHELIUM, EXPEDIENT_TIPUS_ID)
    );

-- Canvi Versio --
ALTER TABLE HEL_VERSIO add DATA_EXECUCIO TIMESTAMP(6);
ALTER TABLE HEL_VERSIO add PROCES_EXECUTAT NUMBER(1);

update HEL_IDGEN set VALOR = VALOR+1 where TAULA = 'hel_versio';
update HEL_VERSIO set PROCES_EXECUTAT = 1, DATA_EXECUCIO = SYSDATE where CODI = 'inicial';
insert into HEL_VERSIO (ID, CODI, ORDRE, PROCES_EXECUTAT, DATA_EXECUCIO) values ((select VALOR from HEL_IDGEN where TAULA = 'hel_versio' ),'2.1.0', 210, 0, SYSDATE);
