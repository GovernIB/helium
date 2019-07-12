--------------------------------------------------------
-- 1240 Nova funcionalitat Repro per a una tasca concreta
--------------------------------------------------------

ALTER TABLE HEL_REPRO ADD TASCA_CODI VARCHAR2(255);

CREATE INDEX HEL_REPRO_TASCA_CODI_I ON HEL_REPRO 
(TASCA_CODI);

ALTER TABLE HEL_TASCA ADD AMB_REPRO NUMBER(1) DEFAULT 0;
UPDATE HEL_TASCA SET AMB_REPRO = 0;

--------------------------------------------------------
-- 1164 Integració amb NOTIB
--------------------------------------------------------

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_ACTIU NUMBER(1) DEFAULT 0;
UPDATE HEL_EXPEDIENT_TIPUS SET NOTIB_ACTIU = 0;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_SEU_UNITATADMIN VARCHAR2(100);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_SEU_CODIPROCEDIMENT VARCHAR2(100);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_SEU_OFICINA VARCHAR2(100);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_SEU_LLIBRE VARCHAR2(100);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_SEU_ORGAN VARCHAR2(100);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_SEU_IDIOMA VARCHAR2(10);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_AVISTITOL VARCHAR2(100);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_AVISTEXT VARCHAR2(1024);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_AVISTEXTSMS VARCHAR2(200);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_OFICITITOL VARCHAR2(256);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_OFICITEXT VARCHAR2(1024);

ALTER TABLE HEL_DOCUMENT ADD NOTIFICABLE NUMBER(1) DEFAULT 0;
UPDATE HEL_DOCUMENT SET NOTIFICABLE = 0;

CREATE TABLE HEL_DOCUMENT_NOTIFICACIO (  
  ID                   	NUMBER(19)			NOT NULL,
  USUARI_CODI			VARCHAR2(255),
  EXPEDIENT_ID         	NUMBER(19)			NOT NULL,
  DOCUMENT_STORE_ID    	NUMBER(19)			NOT NULL,
  CERTIFICACIO_STORE_ID	NUMBER(19),
  EMISOR				VARCHAR2(10),
  TIPUS                	VARCHAR2(255),
  DATA_PROGRAMADA      	TIMESTAMP(6),
  RETARD				NUMBER(10),
  DATA_CADUCITAT      	TIMESTAMP(6),
  TIT_NOM           	VARCHAR2(100),
  TIT_LLINATGE1        	VARCHAR2(100),
  TIT_LLINATGE2        	VARCHAR2(100),
  TIT_NIF        		VARCHAR2(100),
  TIT_TELEFON        	VARCHAR2(16),
  TIT_EMAIL         	VARCHAR2(160),
  DEST_NOM           	VARCHAR2(100),
  DEST_LLINATGE1       	VARCHAR2(100),
  DEST_LLINATGE2       	VARCHAR2(100),
  DEST_NIF        		VARCHAR2(100),
  DEST_TELEFON        	VARCHAR2(16),
  DEST_EMAIL         	VARCHAR2(160),
  IDIOMA               	VARCHAR2(32),
  AVIS_TITOL           	VARCHAR2(256),
  AVIS_TEXT            	VARCHAR2(1024),
  AVIS_TEXT_MOBIL      	VARCHAR2(256),
  OFICI_TITOL          	VARCHAR2(256),
  OFICI_TEXT           	VARCHAR2(1024),
  ENV_ID           		VARCHAR2(100),
  ENV_REF           	VARCHAR2(100),
  ENV_DAT_ESTAT        	VARCHAR2(20),
  ENV_DAT_DATA        	TIMESTAMP(6),
  ENV_DAT_ORIGEN       	VARCHAR2(20),
  ENV_CERT_DATA        	TIMESTAMP(6),
  ENV_CERT_ORIGEN      	VARCHAR2(20),
  ENV_CERT_ARXIUUID		VARCHAR2(50),
  ESTAT                	VARCHAR2(20)		NOT NULL,
  ENVIAT_DATA       	TIMESTAMP(6),
  PROCESSAT_DATA       	TIMESTAMP(6),
  CANCELAT_DATA       	TIMESTAMP(6),
  ERROR         		NUMBER(1),
  ERROR_DESCRIPCIO		VARCHAR2(2048),
  INTENT_NUM			NUMBER(10),
  INTENT_DATA          	TIMESTAMP(6),
  INTENT_PROXIM_DATA	TIMESTAMP(6),
  ENVIAMENT_TIPUS		VARCHAR2(255),
  CONCEPTE				VARCHAR2(256)		NOT NULL,
  DESCRIPCIO			VARCHAR2(256)
);

CREATE TABLE HEL_DOCUMENT_NOTIFICACIO_ANX (
  DOCUMENT_NOTIFICACIO_ID NUMBER(19)	NOT NULL,
  DOCUMENT_STORE_ID NUMBER(19)			NOT NULL
);

ALTER TABLE HEL_DOCUMENT_NOTIFICACIO ADD (
	CONSTRAINT HEL_EXPEDIENT_DOCNOT_FK FOREIGN KEY (EXPEDIENT_ID)
	REFERENCES HEL_EXPEDIENT (ID));
 
ALTER TABLE HEL_DOCUMENT_NOTIFICACIO ADD (PRIMARY KEY (ID));
ALTER TABLE HEL_DOCUMENT_NOTIFICACIO ADD (
	CONSTRAINT HEL_DOCUMENT_DOCNOTIF_FK FOREIGN KEY (DOCUMENT_STORE_ID) 
 	REFERENCES HEL_DOCUMENT_STORE (ID));
 	
 ALTER TABLE HEL_DOCUMENT_NOTIFICACIO ADD (
	CONSTRAINT HEL_CERTIFICACIO_NOTIF_FK FOREIGN KEY (CERTIFICACIO_STORE_ID) 
 	REFERENCES HEL_DOCUMENT_STORE (ID));

-- Grants
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_DOCUMENT_NOTIFICACIO TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_DOCUMENT_NOTIFICACIO_ANX TO WWW_HELIUM;
