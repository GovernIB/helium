-- #1335 Helium com a backoffice de Distribucio

-- Noves columnes a la taula de tipus d'expedient
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_ACTIU BOOLEAN DEFAULT FALSE;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_CODI_PROCEDIMENT VARCHAR(200);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_CODI_ASSUMPTE VARCHAR(20);

-- Noves taules per les anotacions
CREATE TABLE HEL_ANOTACIO (
  ID 						BIGINT 		NOT NULL, 
  EXPEDIENT_TIPUS_ID					BIGINT 		NOT NULL, 
  EXPEDIENT_ID				BIGINT, 
  DISTRIBUCIO_ID				character varying (80) NOT NULL,
  DISTRIBUCIO_CLAU_ACCES		character varying (200) NOT NULL,
  ESTAT						BIGINT               NOT NULL,
  DATA_RECEPCIO				timestamp without time zone NOT NULL, 
  DATA_PROCESSAMENT			timestamp without time zone, 
  REBUIG_MOTIU				character varying (500),
  APLICACIO_CODI 			character varying (20), 
  APLICACIO_VERSIO 			character varying (15), 
  ASSUMPTE_CODI_CODI 		character varying (16), 
  ASSUMPTE_CODI_DESC 		character varying (100), 
  ASSUMPTE_TIPUS_CODI		character varying (16) 	NOT NULL, 
  ASSUMPTE_TIPUS_DESC 		character varying (100), 
  DATA 						timestamp without time zone 	NOT NULL, 
  DOC_FISICA_CODI 			character varying (1), 
  DOC_FISICA_DESC 			character varying (100), 
  ENTITAT_CODI 				character varying (21) 	NOT NULL, 
  ENTITAT_DESC 				character varying (100), 
  EXPEDIENT_NUMERO 			character varying (80), 
  EXPOSA 					text, 
  EXTRACTE					character varying (240),
  PROCEDIMENT_CODI			character varying (20),
  IDENTIFICADOR 			character varying (100) 	NOT NULL, 
  IDIOMA_CODI 				character varying (2) 	NOT NULL, 
  IDIOMA_DESC 				character varying (100), 
  LLIBRE_CODI 				character varying (4) 	NOT NULL, 
  LLIBRE_DESC 				character varying (100), 
  OBSERVACIONS 				character varying (50), 
  OFICINA_CODI 				character varying (21) 	NOT NULL, 
  OFICINA_DESC 				character varying (100), 
  ORIGEN_DATA 				timestamp without time zone, 
  ORIGEN_REGISTRE_NUM 		character varying (80), 
  REF_EXTERNA 				character varying (16), 
  SOLICITA 					text, 
  TRANSPORT_NUM 			character varying (20), 
  TRANSPORT_TIPUS_CODI 		character varying (2), 
  TRANSPORT_TIPUS_DESC 		character varying (100), 
  USUARI_CODI 				character varying (20), 
  USUARI_NOM 				character varying (80), 
  DESTI_CODI 				character varying (21) 	NOT NULL, 
  DESTI_DESCRIPCIO 			character varying (100)
);


CREATE TABLE HEL_ANOTACIO_ANNEX (
	ID 						BIGINT 		NOT NULL, 
	CONTINGUT bytea,
	FIRMA_CONTINGUT bytea,
	FIRMA_PERFIL character varying (4),
	FIRMA_TAMANY integer,
	FIRMA_TIPUS character varying (4),
	NOM character varying (80) NOT NULL,
	FIRMA_NOM character varying(80),
	NTI_FECHA_CAPTURA timestamp without time zone NOT NULL,
	NTI_ORIGEN character varying (20) NOT NULL,
	NTI_TIPO_DOC character varying (20) NOT NULL,
	OBSERVACIONS character varying (50),
	SICRES_TIPO_DOC character varying (20),
	SICRES_VALIDEZ_DOC character varying (30),
	TAMANY integer NOT NULL,
	TIPUS_MIME character varying (255),
	TITOL character varying (200) NOT NULL,
	UUID character varying (100),
	ANOTACIO_ID BIGINT NOT NULL,
	ESTAT character varying (20),
	ERROR character varying (4000),
	NTI_ESTADO_ELABORACIO character varying(50) NOT NULL
);


CREATE TABLE HEL_ANOTACIO_INTERESSAT (
	ID 						BIGINT 		NOT NULL, 
	ADRESA character varying (160),
	CANAL character varying (30),
	CP character varying (5),
	DOC_NUMERO character varying (17),
	DOC_TIPUS character varying (15),
	EMAIL character varying (160),
	LLINATGE1 character varying (30),
	LLINATGE2 character varying (30),
	MUNICIPI_CODI character varying (100),
	NOM character varying (30),
	OBSERVACIONS character varying (160),
	PAIS_CODI character varying (4),
	PROVINCIA_CODI character varying (100),
	RAO_SOCIAL character varying (80),
	TELEFON character varying (20),
	TIPUS character varying (40) NOT NULL,
	REPRESENTANT_ID BIGINT,
	ANOTACIO_ID BIGINT,
	PAIS character varying(200),
	PROVINCIA character varying(200),
	MUNICIPI character varying(200),	
	ORGAN_CODI CHARACTER VARYING(9)
);

ALTER TABLE ONLY HEL_ANOTACIO ADD CONSTRAINT HEL_ANOTACIO_PK PRIMARY KEY (ID);
ALTER TABLE ONLY HEL_ANOTACIO_ANNEX ADD CONSTRAINT HEL_ANOTACIO_ANNEX_PK PRIMARY KEY (ID);
ALTER TABLE ONLY HEL_ANOTACIO_INTERESSAT ADD  CONSTRAINT HEL_ANOTACIO_INTERESSAT_PK PRIMARY KEY (ID);  
  
ALTER TABLE HEL_ANOTACIO_INTERESSAT ADD CONSTRAINT HEL_ANOTACIO_INTERESSAT_FK FOREIGN KEY (ANOTACIO_ID) REFERENCES HEL_ANOTACIO (ID);
ALTER TABLE HEL_ANOTACIO_ANNEX ADD CONSTRAINT HEL_ANOTACIO_ANNEX_FK FOREIGN KEY (ANOTACIO_ID) REFERENCES HEL_ANOTACIO (ID);    
ALTER TABLE HEL_ANOTACIO ADD CONSTRAINT HEL_ET_ANOTACIO_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID);
ALTER TABLE HEL_ANOTACIO ADD CONSTRAINT HEL_EXPEDIENT_ANOTACIO_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID);

CREATE INDEX HEL_ANOTACIO_ANNEX_FK_I ON HEL_ANOTACIO_ANNEX(ANOTACIO_ID);
CREATE INDEX HEL_ANOTACIO_INTER_FK_I ON HEL_ANOTACIO_INTERESSAT(ANOTACIO_ID);
CREATE INDEX HEL_REPRESENTANT_ANOTACIO_FK_I ON HEL_ANOTACIO_INTERESSAT(REPRESENTANT_ID);
CREATE INDEX HEL_ANOTACIO_EXPEDIENT_FK_I ON HEL_ANOTACIO(EXPEDIENT_ID);
CREATE INDEX HEL_ANOTACIO_ET_FK_I ON HEL_ANOTACIO(EXPEDIENT_TIPUS_ID);
