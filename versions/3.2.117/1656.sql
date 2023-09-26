--#1656 Permetre notificar més d'un document i a la vegada en un .zip


-- Oracle
CREATE TABLE HEL_DOCUMENT_CONTINGUT (
  ID NUMBER(19)	NOT NULL,
  DOCUMENT_CONTINGUT NUMBER(19)			NOT NULL
);
--GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_DOCUMENT_CONTINGUT TO WWW_HELIUM;

ALTER TABLE HEL_DOCUMENT_CONTINGUT 	ADD CONSTRAINT HEL_DOCSTORE_DOCCONT_ID_FK FOREIGN KEY (ID) REFERENCES HEL_DOCUMENT_STORE (ID);
ALTER TABLE HEL_DOCUMENT_CONTINGUT 	ADD CONSTRAINT HEL_DOCSTORE_DOCCONT_CONT_FK FOREIGN KEY (DOCUMENT_CONTINGUT) REFERENCES HEL_DOCUMENT_STORE (ID);


-- Postgresql
CREATE TABLE HEL_DOCUMENT_CONTINGUT (
  ID BIGINT	NOT NULL,
  DOCUMENT_CONTINGUT BIGINT			NOT NULL
);

ALTER TABLE HEL_DOCUMENT_CONTINGUT ADD  CONSTRAINT HEL_DOCSTORE_DOCCONT_ID_FK FOREIGN KEY (ID) REFERENCES HEL_DOCUMENT_STORE (ID);
ALTER TABLE HEL_DOCUMENT_CONTINGUT 	ADD CONSTRAINT HEL_DOCSTORE_DOCCONT_CONT_FK FOREIGN KEY (DOCUMENT_CONTINGUT) REFERENCES HEL_DOCUMENT_STORE (ID);
