-- #1661 Accés a expedients per permisos sobre unitats organitzatives

-- Oracle

-- -- Nova taula per les unitats organitzatives
CREATE TABLE HEL_UNITAT_ORGANITZATIVA (
  ID           				NUMBER(19)          NOT NULL,
  CODI 						VARCHAR2(9 CHAR) 		NOT NULL,
  DENOMINACIO 				VARCHAR2(300 CHAR) 		NOT NULL,
  NIF_CIF 					VARCHAR2(9 CHAR),
  CODI_UNITAT_SUPERIOR 		VARCHAR2(9 CHAR),
  CODI_UNITAT_ARREL 		VARCHAR2(9 CHAR),
  DATA_CREACIO_OFICIAL 		TIMESTAMP(6),
  DATA_SUPRESSIO_OFICIAL 	TIMESTAMP(6),
  DATA_EXTINCIO_FUNCIONAL 	TIMESTAMP(6),
  DATA_ANULACIO 	 		TIMESTAMP(6),
  DATA_SINCRONITZACIO 		TIMESTAMP(6),
  DATA_ACTUALITZACIO 		TIMESTAMP(6),
  ESTAT 					VARCHAR2(1 CHAR),
  CODI_PAIS 				VARCHAR2(3 CHAR),
  CODI_COMUNITAT 			VARCHAR2(2 CHAR),
  CODI_PROVINCIA 			VARCHAR2(2 CHAR),
  CODI_POSTAL 				VARCHAR2(5 CHAR),
  NOM_LOCALITAT 			VARCHAR2(50 CHAR),
  LOCALITAT 				VARCHAR2(40 CHAR),
  ADRESSA 					VARCHAR2(70 CHAR),
  TIPUS_VIA 	 			NUMBER(19),
  NOM_VIA 					VARCHAR2(200 CHAR),
  NUM_VIA 					VARCHAR2(100 CHAR),
  TIPUS_TRANSICIO 		    VARCHAR2(12 CHAR),

  CREATEDDATE          		TIMESTAMP(6),
  CREATEDBY_CODI       		VARCHAR2(256 CHAR),
  LASTMODIFIEDDATE     		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(256 CHAR),
  CODI_DIR3_ENTITAT		 	VARCHAR2(9 CHAR)
);

CREATE TABLE HEL_UO_SINC_REL (
  ANTIGA_UO         			NUMBER(19)          NOT NULL,
  NOVA_UO           			NUMBER(19)          NOT NULL
);

ALTER TABLE HEL_UNITAT_ORGANITZATIVA ADD (
  CONSTRAINT HEL_UNITAT_ORGANITZATIVA_PK PRIMARY KEY (ID));
  
ALTER TABLE HEL_UO_SINC_REL ADD (
  CONSTRAINT HEL_UNITAT_ANTIGA_FK FOREIGN KEY (ANTIGA_UO) 
    REFERENCES HEL_UNITAT_ORGANITZATIVA (ID));
ALTER TABLE HEL_UO_SINC_REL ADD (
  CONSTRAINT HEL_UNITAT_NOVA_FK FOREIGN KEY (NOVA_UO) 
    REFERENCES HEL_UNITAT_ORGANITZATIVA (ID));
ALTER TABLE HEL_UO_SINC_REL ADD CONSTRAINT HEL_UO_SINC_REL_MULT_UK UNIQUE (ANTIGA_UO, NOVA_UO);

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD PROCEDIMENT_COMU NUMBER(1);
UPDATE HEL_EXPEDIENT_TIPUS SET PROCEDIMENT_COMU = 0;

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_UNITAT_ORGANITZATIVA TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_UO_SINC_REL TO WWW_HELIUM;

-- Nova taula pels procediments
CREATE TABLE HEL_PROCEDIMENT 
(
  ID						NUMBER(19)	    	NOT NULL,
  CODI          			VARCHAR2(64 CHAR)		NOT NULL,
  NOM						VARCHAR2(256 CHAR),
  CODISIA					VARCHAR2(64 CHAR),
  UNITAT_ORGANITZATIVA_ID   NUMBER(19),
  ESTAT                     VARCHAR2(20 CHAR) DEFAULT 'VIGENT' NOT NULL,
  COMU                      NUMBER DEFAULT 0
);

ALTER TABLE HEL_PROCEDIMENT ADD (
  CONSTRAINT HEL_PROCEDIMENT_PK PRIMARY KEY (ID));

ALTER TABLE HEL_PROCEDIMENT ADD CONSTRAINT HEL_PROCEDIMENT_UNITAT_FK 
	FOREIGN KEY (UNITAT_ORGANITZATIVA_ID) REFERENCES HEL_UNITAT_ORGANITZATIVA(ID);

CREATE INDEX HEL_PROCEDIMENT_CODISIA_I ON HEL_PROCEDIMENT(CODISIA);

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_PROCEDIMENT TO WWW_HELIUM;
	

-- -- Nova taula per la relació de tipus d'expedient i unitats organitzatives per els Permisos
CREATE TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG (
  ID           				NUMBER(19)      NOT NULL,
  EXPEDIENT_TIPUS_ID		NUMBER(19) 		NOT NULL,
  UNITAT_ORGANITZATIVA_ID   NUMBER(19) 		NOT NULL
);

ALTER TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG ADD (
  	CONSTRAINT HEL_EXP_TIPUS_UO_PK PRIMARY KEY (ID)); 

ALTER TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG ADD (
	CONSTRAINT HEL_EXP_TIPUS_EXPTIPUS_UO_FK 
	FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS(ID));

ALTER TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG ADD (
	CONSTRAINT HEL_UNIT_ORG_EXPTIPUS_UO_FK 
	FOREIGN KEY (UNITAT_ORGANITZATIVA_ID) REFERENCES HEL_UNITAT_ORGANITZATIVA(ID));
 
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_EXPEDIENT_TIPUS_UNITAT_ORG TO WWW_HELIUM;

	
-- Insert a la taula de HEL_ACL_ENTRY
INSERT INTO HEL_ACL_CLASS (ID, CLASS) 
VALUES (HEL_ACL_CLASS_SEQ.NEXTVAL, 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa');  
	

-- Nova relació expedient amb unitat organitzativa (Fase 3)
ALTER TABLE HEL_EXPEDIENT ADD (	
		UNITAT_ORGANITZATIVA_ID NUMBER(19));
ALTER TABLE HEL_EXPEDIENT ADD (
	CONSTRAINT HEL_UNIT_ORG_EXPEDIENT_FK
	FOREIGN KEY (UNITAT_ORGANITZATIVA_ID) REFERENCES HEL_UNITAT_ORGANITZATIVA(ID));


-- Nova taula de Paràmetres
CREATE TABLE HEL_PARAMETRE
(
  ID                NUMBER(19)               NOT NULL,
  CODI            	VARCHAR2(512)            NOT NULL,
  NOM		        VARCHAR2(256)			 NOT NULL,
  DESCRIPCIO        VARCHAR2(1024),
  VALOR           	VARCHAR2(256)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_PARAMETRE TO WWW_HELIUM;

INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (1, 
      		 'app.configuracio.propagar.esborrar.expedients',
      		 'Propagar esborrat d''expedients', 
      		 'Si s''habilita es permetrà la propagació de l''esborrat d''expedients quan s''esborri un tipus d''expedient',
      		 '0');
INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (2, 
      		 'app.net.caib.helium.unitats.organitzatives.arrel.codi',
      		 'Codi de l''unitat arrel', 
      		 NULL,
      		 'A04003003');
INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (3, 
      		 'app.net.caib.helium.unitats.organitzatives.data.sincronitzacio',
      		 'Data sincronització Unitats Organitzatives', 
      		 'Data de la primera sincronització d''unitats organitzatives. Indica la data inicial de sincronització.',
      		 NULL);
      		 
INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (4, 
      		 'app.net.caib.helium.unitats.organitzatives.data.actualitzacio',
      		 'Data actualització Unitats Organitzatives', 
      		 'Data de la darrera actualització d''unitats organitzatives. Indica la darrera data d''actualització a partir de la qual es demanen els canvis.'
      		 NULL);
      		 
-- #1723 Afegir un avís quan s'importi un document amb un flux associat que no es troba al tipus d'expedient

-- Nova columa per guardar el nom del flux
ALTER TABLE HEL_DOCUMENT ADD (PORTAFIRMES_FLUX_NOM VARCHAR2(512 CHAR));
