-- #1648 Publicació d'avisos 

CREATE TABLE HEL_AVIS
(
  ID                   NUMBER(19)               NOT NULL,
  ASSUMPTE             VARCHAR2(256)            NOT NULL,
  MISSATGE             VARCHAR2(2048)           NOT NULL,
  DATA_INICI           TIMESTAMP(6)             NOT NULL,
  DATA_FINAL           TIMESTAMP(6)             NOT NULL,
  HORA_INICI	 	   VARCHAR2(5 CHAR),
  HORA_FI		 	   VARCHAR2(5 CHAR),
  ACTIU                NUMBER(1)                NOT NULL,
  AVIS_NIVELL          VARCHAR2(10)             NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6)
);

ALTER TABLE HEL_AVIS ADD (
  CONSTRAINT HEL_AVIS_PK PRIMARY KEY (ID));

CREATE INDEX HEL_AVIS_DATA_INICI_I ON HEL_AVIS(DATA_INICI);
CREATE INDEX HEL_AVIS_DATA_FINAL_I ON HEL_AVIS(DATA_FINAL);

GRANT SELECT ON HEL_AVIS TO WWW_HELIUM;

--1654 Accions sobre les anotacions filtrades
ALTER TABLE HEL_EXEC_MASEXP ADD AUX_ID NUMBER(19,0);

--#1664 El check d'activar tràmits no s'acaba d'exportar bé
--Script per posar el check a true a tots els que no s'hagin importat correctament
UPDATE HEL_EXPEDIENT_TIPUS SET SISTRA_ACTIU = 1 WHERE DISTR_SISTRA = 1 AND SISTRA_ACTIU = 0
