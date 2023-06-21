-- #1682 Error creant expedients a l'Arxiu per nom massa llarg

-- Modificació la columna extracte perquè compti 240 char (no 240 bytes)
ALTER TABLE HEL_ANOTACIO MODIFY EXTRACTE VARCHAR2(240 CHAR);