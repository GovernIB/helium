-- #1661 Accés a expedients per permisos sobre unitats organitzatives

-- Postgesql
	
-- -- Nova taula per les unitats organitzatives
CREATE TABLE HEL_UNITAT_ORGANITZATIVA (
  ID           				BIGSERIAL          			NOT NULL,
  CODI          			character varying(64)		NOT NULL,
  DENOMINACIO 				character varying(300)		NOT NULL,
  NIF_CIF 					character varying(9),
  CODI_UNITAT_SUPERIOR 		character varying(9),
  CODI_UNITAT_ARREL 		character varying(9),
  DATA_CREACIO_OFICIAL 		TIMESTAMP(6),
  DATA_SUPRESSIO_OFICIAL 	TIMESTAMP(6),
  DATA_EXTINCIO_FUNCIONAL 	TIMESTAMP(6),
  DATA_ANULACIO 	 		TIMESTAMP(6),
  DATA_SINCRONITZACIO 		TIMESTAMP(6),
  DATA_ACTUALITZACIO 		TIMESTAMP(6),
  ESTAT 					character varying(1),
  CODI_PAIS 				character varying(3),
  CODI_COMUNITAT 			character varying(2),
  CODI_PROVINCIA 			character varying(2),
  CODI_POSTAL 				character varying(5),
  NOM_LOCALITAT 			character varying(50),
  LOCALITAT 				character varying(40),
  ADRESSA 					character varying(70),
  TIPUS_VIA 	 			BIGSERIAL,
  NOM_VIA 					character varying(200),
  NUM_VIA 					character varying(100),
  TIPUS_TRANSICIO 		    character varying(12),
  CREATEDDATE          		TIMESTAMP(6),
  CREATEDBY_CODI       		character varying(256),
  LASTMODIFIEDDATE     		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		character varying(256),
  CODI_DIR3_ENTITAT		 	character varying(9)
);

CREATE TABLE HEL_UO_SINC_REL (
  ANTIGA_UO         			BIGSERIAL          NOT NULL,
  NOVA_UO           			BIGSERIAL          NOT NULL
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

  
-- Nova taula pels procediments
CREATE TABLE HEL_PROCEDIMENT 
(
  ID						BIGSERIAL	    			NOT NULL,
  CODI          			character varying(64)		NOT NULL,
  NOM						character varying(256),
  CODISIA					character varying(64),
  ID_UNITAT_ORGANITZATIVA	BIGSERIAL,
  ESTAT                     character varying(20) default 'VIGENT' NOT NULL
);

CREATE TABLE HEL_PARAMETRE
(
  ID                BIGSERIAL                		NOT NULL,
  CODI            	character varying(256)          NOT NULL,
  DESCRIPCIO        character varying(256)          NOT NULL,
  VALOR           	character varying(256)          NOT NULL
);

ALTER TABLE HEL_PROCEDIMENT ADD (
  CONSTRAINT HEL_PROCEDIMENT_PK PRIMARY KEY (ID));

ALTER TABLE HEL_PROCEDIMENT ADD CONSTRAINT HEL_PROCEDIMENT_UNITAT_FK 
	FOREIGN KEY (ID_UNITAT_ORGANITZATIVA) REFERENCES HEL_UNITAT_ORGANITZATIVA(ID);
	
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD PROCEDIMENT_COMU BOOLEAN;
UPDATE HEL_EXPEDIENT_TIPUS SET PROCEDIMENT_COMU = false;	

-- -- Nova taula per la relació de tipus d'expedient i unitats organitzatives per els Permisos
CREATE TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG (
  ID           				BIGSERIAL       NOT NULL,
  EXPEDIENT_TIPUS_ID		BIGSERIAL 		NOT NULL,
  UNITAT_ORGANITZATIVA_ID   BIGSERIAL 		NOT NULL
);

ALTER TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG ADD (
  	CONSTRAINT HEL_EXP_TIPUS_UO_PK PRIMARY KEY (ID)); 

ALTER TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG ADD (
	CONSTRAINT HEL_EXP_TIPUS_EXPTIPUS_UO_FK 
	FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS(ID));

ALTER TABLE HEL_EXPEDIENT_TIPUS_UNITAT_ORG ADD (
	CONSTRAINT HEL_UNIT_ORG_EXPTIPUS_UO_FK 
	FOREIGN KEY (UNITAT_ORGANITZATIVA_ID) REFERENCES HEL_UNITAT_ORGANITZATIVA(ID));



-- #1723 Afegir un avís quan s'importi un document amb un flux associat que no es troba al tipus d'expedient

-- Postgesql
	
-- Nova columa per guardar el nom del flux	
ALTER TABLE HEL_DOCUMENT ADD PORTAFIRMES_FLUX_NOM VARCHAR(512);	