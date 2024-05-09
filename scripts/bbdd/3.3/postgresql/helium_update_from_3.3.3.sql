--#1696 Guardar la traça de l'error en el processament d'anotacions

-- Postgresql
ALTER TABLE HEL_ANOTACIO MODIFY ERROR_PROCESSAMENT CHARACTER VARYING(2048);