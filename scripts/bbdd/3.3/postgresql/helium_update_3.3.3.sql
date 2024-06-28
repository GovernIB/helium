--#1696 Guardar la traça de l'error en el processament d'anotacions

-- Postgresql
ALTER TABLE HEL_ANOTACIO ALTER COLUMN ERROR_PROCESSAMENT TYPE text;