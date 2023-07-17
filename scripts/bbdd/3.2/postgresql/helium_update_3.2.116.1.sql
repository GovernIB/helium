
--#1658 Poder afegir un manual d'ajuda per tipus d'expedient
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD MANUAL_AJUDA_CONTENT BYTEA;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD MANUAL_AJUDA_NOM  VARCHAR(1024);

-- #1596 Reconèixer documents firmats en el mapeig amb Sistra2 i evitar duplicitat de documents

-- Nova columna per relacionar documents amb annexos
ALTER TABLE HEL_DOCUMENT_STORE ADD COLUMN ANNEX_ID BIGINT;

-- Actualitza la relació annexos-documents a la inversa
UPDATE HEL_DOCUMENT_STORE ds 
SET ds.ANNEX_ID = 
	(
		SELECT aa.ID 
		FROM HEL_ANOTACIO_ANNEX aa 
		WHERE aa.DOCUMENT_STORE_ID = ds.id
	)
WHERE ds.id IN 
( 
	SELECT DISTINCT DOCUMENT_STORE_ID FROM HEL_ANOTACIO_ANNEX
);
