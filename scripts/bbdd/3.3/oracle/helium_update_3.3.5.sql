-- #1710 Adaptar el manteniment d'interessats als camps SICRES

ALTER TABLE HEL_INTERESSAT MODIFY NOM NULL;
ALTER TABLE HEL_INTERESSAT MODIFY TELEFON VARCHAR2(20 CHAR);
ALTER TABLE HEL_INTERESSAT MODIFY DIR3CODI VARCHAR2(21 CHAR);
ALTER TABLE HEL_INTERESSAT RENAME COLUMN NIF TO DOCUMENTIDENT;
ALTER TABLE HEL_INTERESSAT MODIFY DOCUMENTIDENT VARCHAR2(256 CHAR);

ALTER TABLE HEL_INTERESSAT ADD DIRECCIO VARCHAR2(160 CHAR);
ALTER TABLE HEL_INTERESSAT ADD CODIDIRE VARCHAR2(21 CHAR);
ALTER TABLE HEL_INTERESSAT ADD TIPUSDOCIDENT VARCHAR2(64 CHAR);
ALTER TABLE HEL_INTERESSAT ADD RAOSOCIAL VARCHAR2(256 CHAR);
ALTER TABLE HEL_INTERESSAT ADD ES_REPRESENTANT NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD OBSERVACIONS VARCHAR2(256 CHAR);

ALTER TABLE HEL_INTERESSAT ADD PAIS VARCHAR2(4 CHAR);
ALTER TABLE HEL_INTERESSAT ADD PROVINCIA VARCHAR2(2 CHAR);
ALTER TABLE HEL_INTERESSAT ADD MUNICIPI VARCHAR2(5 CHAR);
ALTER TABLE HEL_INTERESSAT ADD CANALNOTIF VARCHAR2(8 CHAR);

ALTER TABLE HEL_INTERESSAT ADD REPRESENTANT_ID NUMBER(19);

ALTER TABLE HEL_INTERESSAT ADD CONSTRAINT HEL_INTERESSAT_REPRESENTANT_FK FOREIGN KEY (REPRESENTANT_ID) REFERENCES HEL_INTERESSAT(ID) ENABLE;


-- #1741 Afegir documents a partir de consultes predefinides a Pinbal

CREATE TABLE HEL_PINBAL_SERVEI (
    ID NUMBER(19) NOT NULL, 
	CODI VARCHAR2(64 CHAR) NOT NULL,
	NOM VARCHAR2(256 CHAR) NOT NULL, 
	DOC_PERMES_DNI NUMBER(1) NOT NULL, 
	DOC_PERMES_NIF NUMBER(1) NOT NULL, 
	DOC_PERMES_CIF NUMBER(1) NOT NULL, 
	DOC_PERMES_NIE NUMBER(1) NOT NULL, 
	DOC_PERMES_PAS NUMBER(1) NOT NULL,
	CREATEDDATE TIMESTAMP (6),
	UPDATEDDATE TIMESTAMP (6),
	UPDATEDUSUARI VARCHAR2(64 BYTE),
	ACTIU NUMBER(1) NOT NULL
);

ALTER TABLE HEL_PINBAL_SERVEI ADD (CONSTRAINT HEL_PINBAL_SERVEI_PK PRIMARY KEY (ID));

--Inicialització
REM INSERTING into HEL_PINBAL_SERVEI
SET DEFINE OFF;
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('1','SVDDGPCIWS02', 'Consulta de dades d''identitat', '1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('2','SVDDGPVIWS02','Verificació de dades d''identitat', '1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('3','SVDCCAACPASWS01','Estar al corrent d''obligacions tributàries per a la sol·licitut de subvencions i ajudes de la CCAA','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('4','SVDSCDDWS01','Servei de consulta de dades de discapacitat','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('5','SCDCPAJU','Servei de consulta de padró de convivència','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('6','SVDSCTFNWS01','Servei de consulta de família nombrosa','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('7','SVDCCAACPCWS01','Estar al corrent d''obligacions tributàries per a la contractació amb la CCAA','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('8','Q2827003ATGSS001','Estar al corrent de pagament amb la Seguretat Social','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('9','SVDDELSEXWS01','Consulta d''inexistència de delictes sexuals per dades de filiació','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('10','SCDHPAJU','Servei de consulta de padró històric','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('11','NIVRENTI','Consulta del nivell de renta','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('12','ECOT103','Estar al corrent d''obligacions tributàries per a la sol·licitut de subvencions i ajudes amb indicació d''incompliments','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('13','SVDDGPRESIDENCIALEGALDOCWS01','Servei de consulta de dades de residència legal d''estrangers per documentació','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('14','SVDRRCCNACIMIENTOWS01','Servei de consulta de naixement','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('15','SVDRRCCMATRIMONIOWS01','Servei de consulta de matrimoni','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('16','SVDRRCCDEFUNCIONWS01','Servei de consulta de defunció','1','1','1','1','1',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('17','SVDBECAWS01','Servei de consulta de condició de becat','1','1','1','1','1',sysdate, 1);

ALTER TABLE hel_document ADD PINBAL_ACTIU NUMBER(1) DEFAULT 0;
ALTER TABLE hel_document ADD PINBAL_FINALITAT VARCHAR2(250 CHAR);
ALTER TABLE hel_document ADD PINBAL_SERVEI NUMBER(19);
ALTER TABLE hel_document ADD PINBAL_CIFORGAN NUMBER(1) DEFAULT 0;

UPDATE hel_document set PINBAL_ACTIU=0, PINBAL_CIFORGAN=0;


--#1782 Sincronització amb l'Arxiu

ALTER TABLE HEL_EXPEDIENT ADD (ERROR_ARXIU CLOB);

    
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_PINBAL_SERVEI TO WWW_HELIUM;