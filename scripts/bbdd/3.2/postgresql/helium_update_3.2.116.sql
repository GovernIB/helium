-- #1648 Publicació d'avisos 
CREATE TABLE HEL_AVIS
(
  ID                   BIGSERIAL               			 NOT NULL,
  ASSUMPTE             character varying(256)            NOT NULL,
  MISSATGE             character varying(2048)           NOT NULL,
  DATA_INICI           timestamp without time zone       NOT NULL,
  DATA_FINAL           timestamp without time zone       NOT NULL,
  HORA_INICI	 	   character varying(5),
  HORA_FI		 	   character varying(5),
  ACTIU                boolean                		     NOT NULL,
  AVIS_NIVELL          character varying(10)             NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);

ALTER TABLE ONLY HEL_AVIS ADD CONSTRAINT HEL_AVIS_PK PRIMARY KEY (ID);

CREATE INDEX HEL_AVIS_DATA_INICI_I ON HEL_AVIS(DATA_INICI);
CREATE INDEX HEL_AVIS_DATA_FINAL_I ON HEL_AVIS(DATA_FINAL);


--1654 Accions sobre les anotacions filtrades
ALTER TABLE HEL_EXEC_MASEXP ADD COLUMN AUX_ID BIGINT;

--#1664 El check d'activar tràmits no s'acaba d'exportar bé
--Script per posar el check a true a tots els que no s'hagin importat correctament
UPDATE HEL_EXPEDIENT_TIPUS SET SISTRA_ACTIU = TRUE WHERE DISTR_SISTRA = TRUE AND SISTRA_ACTIU = FALSE