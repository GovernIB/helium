-- #1682 Error creant expedients a l'Arxiu per nom massa llarg

-- Modificació la columna extracte perquè compti 240 char (no 240 bytes)
ALTER TABLE HEL_ANOTACIO MODIFY EXTRACTE VARCHAR2(240 CHAR);

--#1658 Poder afegir un manual d'ajuda per tipus d'expedient
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD MANUAL_AJUDA_CONTENT BLOB;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD MANUAL_AJUDA_NOM VARCHAR2(1024 CHAR);

-- #1596 Reconèixer documents firmats en el mapeig amb Sistra2 i evitar duplicitat de documents

-- Nova columna per relacionar documents amb annexos
ALTER TABLE HEL_DOCUMENT_STORE ADD (ANNEX_ID NUMBER(19));

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