-- #1428 No funciona "reintentar processament" pels annexos de les anotacions de Distribucio  
-- Afegir HEL_ANOTACIO_ANNEX.DOCUMENT_STORE_ID per quan l'annex s'incorpora a l'expedient

-- ORACLE 
ALTER TABLE HEL_ANOTACIO_ANNEX ADD DOCUMENT_STORE_ID NUMBER(19);

-- POSTGRESQL
ALTER TABLE HEL_ANOTACIO_ANNEX ADD DOCUMENT_STORE_ID BIGINT;