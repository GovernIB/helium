-- #1648 Publicació d'avisos 

-- ORACLE

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

-- Postgesql

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

