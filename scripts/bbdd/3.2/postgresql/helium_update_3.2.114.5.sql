-- #1593 Assenyalar documents amb firma invàlida o estat esborrany provinents de Distribucio

ALTER TABLE HEL_ANOTACIO_ANNEX ADD DOCUMENT_VALID BOOLEAN;
ALTER TABLE HEL_ANOTACIO_ANNEX ADD DOCUMENT_ERROR character varying (1000);
ALTER TABLE HEL_ANOTACIO_ANNEX ADD ARXIU_ESTAT character varying (20);

ALTER TABLE HEL_DOCUMENT_STORE ADD DOCUMENT_VALID BOOLEAN;
ALTER TABLE HEL_DOCUMENT_STORE ADD DOCUMENT_ERROR character varying (1000);