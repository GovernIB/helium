-- Llistat amb la informació pel llistat d'expedients
CREATE TABLE HEL_EXPEDIENT
(
    ID                  BIGINT                NOT NULL,
    ENTORN_ID           BIGINT                NOT NULL,
    EXPEDIENT_TIPUS_ID  BIGINT                NOT NULL,
    PROCESS_INSTANCE_ID VARCHAR(255)          NOT NULL,
    NUMERO              VARCHAR(64)           NOT NULL,
    NUMERO_DEFAULT      VARCHAR(64)           NOT NULL,
    TITOL               VARCHAR(255),
    DATA_INICI          TIMESTAMP(6)          NOT NULL,
    DATA_FI             TIMESTAMP(6),
    ESTAT_TIPUS         VARCHAR(16)           NOT NULL,
    ESTAT_ID            BIGINT,
  	ATURAT              BOOLEAN               NOT NULL DEFAULT false,
    INFO_ATURAT         VARCHAR(1024),
  	ANULAT              BOOLEAN               NOT NULL DEFAULT false,
    COMENTARI_ANULAT    VARCHAR(255),
    ALERTES_TOTALS      BIGINT                NOT NULL DEFAULT 0,
    ALERTES_PENDENTS    BIGINT                NOT NULL DEFAULT 0,
  	AMB_ERRORS          BOOLEAN               NOT NULL DEFAULT false
);

ALTER TABLE HEL_EXPEDIENT ADD CONSTRAINT HEL_EXPEDIENT_PK PRIMARY KEY (ID);
CREATE INDEX HEL_EXPEDIENT_EXPTIP_I ON HEL_EXPEDIENT (EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_EXPEDIENT_ENTORN_I ON HEL_EXPEDIENT (ENTORN_ID);

-- Llistat amb la informació dels processos
CREATE TABLE HEL_PROCES
(
    ID                  VARCHAR(64)     NOT NULL,
    EXPEDIENT_ID        BIGINT          NOT NULL,
    PROCES_ARREL_ID     VARCHAR(64)     NOT NULL,
    PROCES_PARE_ID      VARCHAR(64),
    PROCESSDEFINITION   VARCHAR(64)     NOT NULL,
    DESCRIPCIO          VARCHAR(255),
    DATA_INICI          TIMESTAMP(6)    NOT NULL,
    DATA_FI             TIMESTAMP(6),
    SUSPES              BOOLEAN         NOT NULL DEFAULT false
);

ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_PK PRIMARY KEY (ID));
CREATE INDEX HEL_PROCES_EXP_I ON HEL_PROCES (EXPEDIENT_ID);
ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_EXP_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID));
ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_ARREL_FK FOREIGN KEY (PROCES_ARREL_ID) REFERENCES HEL_PROCES (ID));
ALTER TABLE HEL_PROCES ADD (CONSTRAINT HEL_PROCES_PARE_FK FOREIGN KEY (PROCES_PARE_ID) REFERENCES HEL_PROCES (ID));


-- Llistat amb la informació pel llistat de tasques
CREATE TABLE HEL_TASCA
(
    ID                  VARCHAR(64)           NOT NULL,
    EXPEDIENT_ID        BIGINT                NOT NULL,
    PROCES_ID           VARCHAR(64)           NOT NULL,
    NOM                 VARCHAR(255)          NOT NULL,
    TITOL               VARCHAR(255)          NOT NULL,
    AGAFADA             BOOLEAN               NOT NULL DEFAULT false,
    CANCELADA           BOOLEAN               NOT NULL DEFAULT false,
    SUSPESA             BOOLEAN               NOT NULL DEFAULT false,
    COMPLETADA          BOOLEAN               NOT NULL DEFAULT false,
    ASSIGNADA           BOOLEAN               NOT NULL DEFAULT false,
    MARCADA_FINALITZAR  TIMESTAMP(6)               NOT NULL DEFAULT false,
    ERROR_FINALITZACIO  VARCHAR(1000),
    DATA_FINS           TIMESTAMP(6),
    DATA_FI				TIMESTAMP(6),
   	INICI_FINALITZACIO  TIMESTAMP(6),
    DATA_CREACIO        TIMESTAMP(6)          NOT NULL,
    USUARI_ASSIGNAT     VARCHAR(255),
    GRUP_ASSIGNAT       VARCHAR(255),
    PRIORITAT           INTEGER               NOT NULL DEFAULT 3
);

ALTER TABLE HEL_TASCA ADD CONSTRAINT HEL_TASCA_PK PRIMARY KEY (ID);
CREATE INDEX HEL_TASCA_EXP_I ON HEL_TASCA (EXPEDIENT_ID);
ALTER TABLE ONLY HEL_TASCA ADD CONSTRAINT HEL_TASCA_EXP_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID);


-- Taula de representants amb una clau composta tascaId, usuariCodi
CREATE TABLE HEL_RESPONSABLE
(
  ID                   BIGSERIAL                       NOT NULL,
  TASCA_ID             VARCHAR(64)                     NOT NULL,
  USUARI_CODI          character varying(255)          NOT NULL
);

ALTER TABLE HEL_RESPONSABLE ADD CONSTRAINT HEL_RESPONSABLE_PK PRIMARY KEY (ID);
CREATE INDEX HEL_RESP_TASCA_I ON HEL_RESPONSABLE (TASCA_ID);
ALTER TABLE ONLY HEL_RESPONSABLE ADD CONSTRAINT HEL_RESP_UK UNIQUE (TASCA_ID, USUARI_CODI);

ALTER TABLE ONLY HEL_RESPONSABLE ADD CONSTRAINT HEL_RESP_TASCA_FK FOREIGN KEY (TASCA_ID) REFERENCES HEL_TASCA (ID);

CREATE SEQUENCE HEL_RESPONSABLE_SEQ START WITH 10000000;

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_EXPEDIENT TO HELIUM_MS_EXPEDIENT;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_PROCES TO HELIUM_MS_EXPEDIENT;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_TASCA TO HELIUM_MS_EXPEDIENT;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_RESPONSABLE TO HELIUM_MS_EXPEDIENT;
GRANT SELECT ON HEL_RESPONSABLE_SEQ TO HELIUM_MS_EXPEDIENT;

