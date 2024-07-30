CREATE TABLE HEL_PINBAL_SERVEI 
   (ID NUMBER(38,0) NOT NULL ENABLE, 
	CODI VARCHAR2(64 CHAR) NOT NULL ENABLE,
	NOM VARCHAR2(256 CHAR) NOT NULL ENABLE, 
	DOC_PERMES_DNI NUMBER(1,0) NOT NULL ENABLE, 
	DOC_PERMES_NIF NUMBER(1,0) NOT NULL ENABLE, 
	DOC_PERMES_CIF NUMBER(1,0) NOT NULL ENABLE, 
	DOC_PERMES_NIE NUMBER(1,0) NOT NULL ENABLE, 
	DOC_PERMES_PAS NUMBER(1,0) NOT NULL ENABLE,
	CREATEDDATE TIMESTAMP (6),
	UPDATEDDATE TIMESTAMP (6),
	UPDATEDUSUARI VARCHAR2(64 BYTE),
	ACTIU NUMBER(1,0) NOT NULL ENABLE);

ALTER TABLE HEL_PINBAL_SERVEI ADD (CONSTRAINT HEL_PINBAL_SERVEI_PK PRIMARY KEY (ID));

--Inicialització
REM INSERTING into HEL_PINBAL_SERVEI
SET DEFINE OFF;
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('1','SVDDGPCIWS02', 'Consulta de dades d''identitat', '0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('2','SVDDGPVIWS02','Verificació de dades d''identitat', '0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('3','SVDCCAACPASWS01','Estar al corrent d''obligacions tributàries per a la sol·licitut de subvencions i ajudes de la CCAA','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('4','SVDSCDDWS01','Servei de consulta de dades de discapacitat','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('5','SCDCPAJU','Servei de consulta de padró de convivència','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('6','SVDSCTFNWS01','Servei de consulta de família nombrosa','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('7','SVDCCAACPCWS01','Estar al corrent d''obligacions tributàries per a la contractació amb la CCAA','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('8','Q2827003ATGSS001','Estar al corrent de pagament amb la Seguretat Social','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('9','SVDDELSEXWS01','Consulta d''inexistència de delictes sexuals per dades de filiació','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('10','SCDHPAJU','Servei de consulta de padró històric','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('11','NIVRENTI','Consulta del nivell de renta','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('12','ECOT103','Estar al corrent d''obligacions tributàries per a la sol·licitut de subvencions i ajudes amb indicació d''incompliments','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('13','SVDDGPRESIDENCIALEGALDOCWS01','Servei de consulta de dades de residència legal d''estrangers per documentació','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('14','SVDRRCCNACIMIENTOWS01','Servei de consulta de naixement','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('15','SVDRRCCMATRIMONIOWS01','Servei de consulta de matrimoni','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('16','SVDRRCCDEFUNCIONWS01','Servei de consulta de defunció','0','0','0','0','0',sysdate, 1);
Insert into HEL_PINBAL_SERVEI (ID,CODI,NOM,DOC_PERMES_DNI,DOC_PERMES_NIF,DOC_PERMES_CIF,DOC_PERMES_NIE,DOC_PERMES_PAS,CREATEDDATE, ACTIU) values ('17','SVDBECAWS01','Servei de consulta de condició de becat','0','0','0','0','0',sysdate, 1);

ALTER TABLE hel_document ADD PINBAL_ACTIU NUMBER(1,0) DEFAULT 0;
ALTER TABLE hel_document ADD PINBAL_FINALITAT VARCHAR2(250 CHAR);
ALTER TABLE hel_document ADD PINBAL_SERVEI NUMBER(38,0);
ALTER TABLE hel_document ADD PINBAL_CIFORGAN NUMBER(1, 0) DEFAULT 0;

UPDATE hel_document set PINBAL_ACTIU=0, PINBAL_CIFORGAN=0;