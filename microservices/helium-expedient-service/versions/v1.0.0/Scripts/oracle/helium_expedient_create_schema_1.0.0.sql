-- Llistat amb la informació pel llistat d'expedients
CREATE TABLE HEL_EXPEDIENT
(
    ID                  NUMBER(19)                NOT NULL,
    ENTORN_ID           NUMBER(19)                NOT NULL,
    EXPEDIENT_TIPUS_ID  NUMBER(19)                NOT NULL,
    PROCESS_INSTANCE_ID VARCHAR2(255 CHAR)        NOT NULL,
    NUMERO              VARCHAR2(64 CHAR)         NOT NULL,
    NUMERO_DEFAULT      VARCHAR2(64 CHAR)         NOT NULL,
    TITOL               VARCHAR2(255 CHAR),
    DATA_INICI          TIMESTAMP(6)              NOT NULL,
    DATA_FI             TIMESTAMP(6),
    ESTAT_TIPUS         VARCHAR2(16 CHAR)         NOT NULL,
    ESTAT_ID            NUMBER(19),
    ATURAT              NUMBER(1)                 DEFAULT 0 NOT NULL,
    INFO_ATURAT         VARCHAR2(1024 CHAR),
    ANULAT              NUMBER(1)                 DEFAULT 0 NOT NULL,
    COMENTARI_ANULAT    VARCHAR2(255 CHAR),
    ALERTES_TOTALS      NUMBER(19)                DEFAULT 0 NOT NULL,
    ALERTES_PENDENTS    NUMBER(19)                DEFAULT 0 NOT NULL,
    AMB_ERRORS          NUMBER(1)                 DEFAULT 0 NOT NULL
);

ALTER TABLE HEL_EXPEDIENT ADD (CONSTRAINT HEL_EXPEDIENT_PK PRIMARY KEY (ID));
CREATE INDEX HEL_EXPEDIENT_EXPTIP_I ON HEL_EXPEDIENT (EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_EXPEDIENT_ENTORN_I ON HEL_EXPEDIENT (ENTORN_ID);

-- Llistat amb la informació dels processos
CREATE TABLE HEL_PROCES
(
    ID                  VARCHAR2(64 CHAR)     NOT NULL,
    EXPEDIENT_ID        NUMBER(19),
    PROCES_ARREL_ID     VARCHAR2(64 CHAR)     NOT NULL,
    PROCES_PARE_ID      VARCHAR2(64 CHAR),
    PROCESSDEFINITION   VARCHAR2(64 CHAR)     NOT NULL,
    DESCRIPCIO          VARCHAR2(255 CHAR),
    DATA_INICI          TIMESTAMP(6)          NOT NULL,
    DATA_FI             TIMESTAMP(6),
    SUSPES              NUMBER(1)             DEFAULT 0 NOT NULL
);

ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_PK PRIMARY KEY (ID));
CREATE INDEX HEL_PROCES_EXP_I ON HEL_PROCES (EXPEDIENT_ID);
ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_EXP_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID));
ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_ARREL_FK FOREIGN KEY (PROCES_ARREL_ID) REFERENCES HEL_PROCES (ID));
ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_PARE_FK FOREIGN KEY (PROCES_PARE_ID) REFERENCES HEL_PROCES (ID));

-- Llistat amb la informació pel llistat de tasques
CREATE TABLE HEL_TASCA
(
    ID                  VARCHAR2(64 CHAR)     NOT NULL,
    EXPEDIENT_ID        NUMBER(19),
    PROCES_ID           VARCHAR2(64 CHAR)     NOT NULL,
    NOM                 VARCHAR(255)          NOT NULL,
    TITOL               VARCHAR(255)          NOT NULL,
    AGAFADA             NUMBER(1)             DEFAULT 0 NOT NULL,
    CANCELADA           NUMBER(1)             DEFAULT 0 NOT NULL,
    SUSPESA             NUMBER(1)             DEFAULT 0 NOT NULL,
    COMPLETADA          NUMBER(1)             DEFAULT 0 NOT NULL,
    ASSIGNADA           NUMBER(1)             DEFAULT 0 NOT NULL,
    MARCADA_FINALITZAR  TIMESTAMP(6),
    ERROR_FINALITZACIO  VARCHAR2(1000 CHAR),
    DATA_FINS           TIMESTAMP(6),
    DATA_FI   			TIMESTAMP(6),
    INICI_FINALITZACIO  TIMESTAMP(6),
    DATA_CREACIO        TIMESTAMP(6)          NOT NULL,
    USUARI_ASSIGNAT      VARCHAR2(255 CHAR),
    GRUP_ASSIGNAT       VARCHAR2(255 CHAR),
    PRIORITAT  			NUMBER(10)            DEFAULT 3 NOT NULL
);

ALTER TABLE HEL_TASCA ADD (CONSTRAINT HEL_TASCA_PK PRIMARY KEY (ID));
CREATE INDEX HEL_TASCA_EXP_I ON HEL_TASCA (EXPEDIENT_ID);
ALTER TABLE HEL_TASCA ADD (CONSTRAINT HEL_TASCA_EXP_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID));
ALTER TABLE HEL_TASCA ADD (CONSTRAINT HEL_TASCA_PROC_FK FOREIGN KEY (PROCES_ID) REFERENCES HEL_PROCES (ID));

-- Taula de representants amb una clau composta tascaId, usuariCodi
CREATE TABLE HEL_RESPONSABLE
(
  ID                   NUMBER(19)                      NOT NULL,
  TASCA_ID             VARCHAR2(64 CHAR)               NOT NULL,
  USUARI_CODI          VARCHAR2(255 CHAR)              NOT NULL
);

ALTER TABLE HEL_RESPONSABLE ADD (CONSTRAINT HEL_RESPONSABLE_PK PRIMARY KEY (ID));
CREATE INDEX HEL_RESP_TASCA_I ON HEL_RESPONSABLE (TASCA_ID);
ALTER TABLE HEL_RESPONSABLE ADD (CONSTRAINT HEL_RESP_TASCA_FK FOREIGN KEY (TASCA_ID) REFERENCES HEL_TASCA (ID));

ALTER TABLE HEL_RESPONSABLE ADD (CONSTRAINT HEL_RESP_UK UNIQUE (TASCA_ID, USUARI_CODI));

CREATE SEQUENCE HEL_RESPONSABLE_SEQ START WITH 10000000;

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_EXPEDIENT TO HELIUM_MS_EXPEDIENT;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_PROCES TO HELIUM_MS_EXPEDIENT;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_TASCA TO HELIUM_MS_EXPEDIENT;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_RESPONSABLE TO HELIUM_MS_EXPEDIENT;
GRANT SELECT ON HEL_RESPONSABLE_SEQ TO HELIUM_MS_EXPEDIENT;

