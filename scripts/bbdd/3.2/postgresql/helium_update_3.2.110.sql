-- #1428 No funciona "reintentar processament" pels annexos de les anotacions de Distribucio 
-- Afegir HEL_ANOTACIO_ANNEX.DOCUMENT_STORE_ID per quan l'annex s'incorpora a l'expedient
-- Incrementa les columnes HEL_ANOTACIO_ANNEX FIRMA_TIPUS i FIRMA_PEFIL a VARCHAR2(30 CHAR)

ALTER TABLE HEL_ANOTACIO_ANNEX ADD DOCUMENT_STORE_ID BIGINT;

ALTER TABLE HEL_ANOTACIO_ANNEX ALTER COLUMN FIRMA_TIPUS TYPE character varying(30);
ALTER TABLE HEL_ANOTACIO_ANNEX ALTER COLUMN FIRMA_PERFIL TYPE character varying(30);