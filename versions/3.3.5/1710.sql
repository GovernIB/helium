-- #1710 Adaptar el manteniment d'interessats als camps SICRES

-- Oracle

ALTER TABLE HEL_INTERESSAT MODIFY NOM NULL;
ALTER TABLE HEL_INTERESSAT MODIFY TELEFON VARCHAR2(20 CHAR);
ALTER TABLE HEL_INTERESSAT MODIFY DIR3CODI VARCHAR2(21 CHAR);
ALTER TABLE HEL_INTERESSAT RENAME COLUMN NIF TO DOCUMENTIDENT;--MARTA recordar esborrar la columna NIF a la bbdd de la 231!!!
ALTER TABLE HEL_INTERESSAT MODIFY DOCUMENTIDENT VARCHAR2(256 CHAR);

ALTER TABLE HEL_INTERESSAT ADD DIRECCIO VARCHAR2(160 CHAR);
ALTER TABLE HEL_INTERESSAT ADD CODIDIRE VARCHAR2(21 CHAR);
ALTER TABLE HEL_INTERESSAT ADD TIPUSDOCIDENT VARCHAR2(64 CHAR);
ALTER TABLE HEL_INTERESSAT ADD RAOSOCIAL VARCHAR2(256 CHAR);
ALTER TABLE HEL_INTERESSAT ADD ES_REPRESENTANT NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD OBSERVACIONS VARCHAR2(256 CHAR);

ALTER TABLE HEL_INTERESSAT ADD PAIS VARCHAR2(4 CHAR);
ALTER TABLE HEL_INTERESSAT ADD PROVINCIA VARCHAR2(2 CHAR);
ALTER TABLE HEL_INTERESSAT ADD MUNICIPI VARCHAR2(5 CHAR);
ALTER TABLE HEL_INTERESSAT ADD CANALNOTIF VARCHAR2(8 CHAR);

ALTER TABLE HEL_INTERESSAT ADD REPRESENTANT_ID NUMBER(19);

ALTER TABLE HEL_INTERESSAT ADD CONSTRAINT HEL_INTERESSAT_REPRESENTANT_FK FOREIGN KEY (REPRESENTANT_ID) REFERENCES HEL_INTERESSAT(ID) ENABLE;


 
    
-- Postgresql

ALTER TABLE HEL_INTERESSAT ALTER COLUMN NOM DROP NOT NULL;
ALTER TABLE HEL_INTERESSAT MODIFY TELEFON CHARACTER VARYING(20);
ALTER TABLE HEL_INTERESSAT MODIFY DIR3CODI CHARACTER VARYING(21);
ALTER TABLE HEL_INTERESSAT RENAME COLUMN NIF TO DOCUMENTIDENT;
ALTER TABLE HEL_INTERESSAT MODIFY DOCUMENTIDENT VARCHAR(256 CHAR);

ALTER TABLE HEL_INTERESSAT ADD DIRECCIO VARCHAR(160);
ALTER TABLE HEL_INTERESSAT ADD CODIDIRE VARCHAR(21);
ALTER TABLE HEL_INTERESSAT ADD TIPUSDOCIDENT VARCHAR(64);
ALTER TABLE HEL_INTERESSAT ADD RAOSOCIAL VARCHAR(256);
ALTER TABLE HEL_INTERESSAT ADD ES_REPRESENTANT BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD OBSERVACIONS VARCHAR(256);

ALTER TABLE HEL_INTERESSAT ADD PAIS VARCHAR(4);
ALTER TABLE HEL_INTERESSAT ADD PROVINCIA VARCHAR(2);
ALTER TABLE HEL_INTERESSAT ADD MUNICIPI VARCHAR(5);
ALTER TABLE HEL_INTERESSAT ADD CANALNOTIF VARCHAR(8);

ALTER TABLE HEL_INTERESSAT ADD REPRESENTANT_ID BIGINT;

ALTER TABLE HEL_INTERESSAT ADD CONSTRAINT HEL_INTERESSAT_REPRESENTANT_FK FOREIGN KEY (REPRESENTANT_ID) REFERENCES HEL_INTERESSAT(ID);


